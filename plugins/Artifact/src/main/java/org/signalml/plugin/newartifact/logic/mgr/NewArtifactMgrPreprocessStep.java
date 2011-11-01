/**
 *
 */
package org.signalml.plugin.newartifact.logic.mgr;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.method.ComputationException;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.exception.PluginToolAbortException;
import org.signalml.plugin.exception.PluginToolInterruptedException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.io.IPluginDataSourceReader;
import org.signalml.plugin.method.logic.AbstractPluginComputationMgrStep;
import org.signalml.plugin.method.logic.IPluginComputationMgrStepTrackerProxy;
import org.signalml.plugin.method.logic.PluginWorkerSet;
import org.signalml.plugin.newartifact.data.INewArtifactSignalReaderWorkerData;
import org.signalml.plugin.newartifact.data.NewArtifactAlgorithmWorkerData;
import org.signalml.plugin.newartifact.data.NewArtifactComputationType;
import org.signalml.plugin.newartifact.data.NewArtifactConstants;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrStepData;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrStepResult;
import org.signalml.plugin.newartifact.io.INewArtifactAlgorithmWriter;
import org.signalml.plugin.newartifact.io.NewArtifactAlgorithmWriter;
import org.signalml.plugin.newartifact.io.NewArtifactDoubleFileAlgorithmWriter;
import org.signalml.plugin.newartifact.io.NewArtifactSignalReaderWorker;
import org.signalml.plugin.signal.PluginSignalHelper;

/**
 * @author kdr
 *
 */
public class NewArtifactMgrPreprocessStep extends
	AbstractPluginComputationMgrStep<NewArtifactMgrStepData> {

	private final int INPUT_BUFFER_QUEUE_SIZE = 40;

	private PluginWorkerSet workers;
	private Collection<INewArtifactAlgorithmWriter> writers;
	private INewArtifactAlgorithmBufferSynchronizer synchronizer;
	private final BlockingQueue<double[][]> readyBuffersQueue;
	private final BlockingQueue<double[][]> freeBuffersQueue;
	private final AtomicBoolean shutdownFlag;
	private Timer timer;
	private TrackerUpdater updater;

	private interface INewArtifactAlgorithmBufferSynchronizer {

		public void addBuffer(double buffer[][]);

		public double[][] getNextBufferForObject(Object sender)
		throws InterruptedException;

		public void markCurrentBufferAsProcessed(Object sender);
	}

	private class NewArtifactAlgorithmBufferSynchronizer implements
		INewArtifactAlgorithmBufferSynchronizer {
		private final List<double[][]> bufferList = new LinkedList<double[][]>();
		private final Map<double[][], Integer> useCount = new HashMap<double[][], Integer>();
		private final Map<Object, Integer> nextBuffer = new HashMap<Object, Integer>();
		private final Map<Object, Semaphore> waitSemaphore = new HashMap<Object, Semaphore>();
		private final BlockingQueue<double[][]> freeBuffersQueue;
		private final AtomicBoolean shutdownFlag;

		public NewArtifactAlgorithmBufferSynchronizer(
			BlockingQueue<double[][]> freeBuffersQueue,
			AtomicBoolean shutdownFlag) {
			this.freeBuffersQueue = freeBuffersQueue;
			this.shutdownFlag = shutdownFlag;
		}

		@Override
		public void addBuffer(double buffer[][]) {
			synchronized (this) {
				bufferList.add(buffer);
				for (Semaphore s : waitSemaphore.values()) {
					s.release();
				}
				useCount.put(buffer, waitSemaphore.size());
			}
		}

		@Override
		public double[][] getNextBufferForObject(Object sender)
				throws InterruptedException {
			final Semaphore s;
			synchronized (this) {
				if (!waitSemaphore.containsKey(sender)) {
					waitSemaphore.put(sender, new Semaphore(bufferList.size()));
					nextBuffer.put(sender, 0);
					for (double[][] buffer : bufferList) {
						useCount.put(buffer, useCount.get(buffer) + 1);
					}
				}

				s = waitSemaphore.get(sender);
			}
			s.acquire();
			synchronized (this) {
				Integer index = nextBuffer.get(sender);
				double buffer[][] = bufferList.get(index);
				if (buffer == null || buffer.length == 0) {
					s.release();
					return null;
				}
				nextBuffer.put(sender, index + 1);
				return buffer;
			}
		}

		@Override
		public void markCurrentBufferAsProcessed(Object sender) {
			double buffer[][];
			Integer count;
			synchronized (this) {
				Integer index = nextBuffer.get(sender);
				if (index == null) {
					return;
				}
				buffer = bufferList.get(index - 1);
				count = useCount.get(buffer);
				if (count == null) {
					return;
				}
				if (count == 1) {
					useCount.remove(buffer);
					bufferList.remove(index - 1);
					for (Entry<Object, Integer> entry : nextBuffer.entrySet()) {
						entry.setValue(entry.getValue() - 1);
					}
				} else {
					useCount.put(buffer, count - 1);
				}
			}

			if (count == 1 && !this.shutdownFlag.get()) {
				try {
					this.freeBuffersQueue.put(buffer);
				} catch (InterruptedException e) {
					return;
				}
			}
		}

	}

	private class NewArtifactAlgorithmDataSource implements
		IPluginDataSourceReader {

		private INewArtifactAlgorithmBufferSynchronizer synchronizer;
		private double currentBuffer[][];

		public NewArtifactAlgorithmDataSource(
			INewArtifactAlgorithmBufferSynchronizer synchronizer) {
			this.synchronizer = synchronizer;
			this.currentBuffer = null;
		}

		@Override
		public void getSample(double[][] buffer) throws InterruptedException {
			if (this.currentBuffer == null) {
				this.fetchNextBuffer();
			}

			if (this.currentBuffer != null) {
				this.copyChunk(buffer);
				this.synchronizer.markCurrentBufferAsProcessed(this);
				this.currentBuffer = null;
			}
		}

		@Override
		public boolean hasMoreSamples() throws InterruptedException {
			if (this.currentBuffer == null) {
				this.fetchNextBuffer();
			}
			return this.currentBuffer != null;
		}

		private void fetchNextBuffer() throws InterruptedException {
			this.currentBuffer = this.synchronizer.getNextBufferForObject(this);
		}

		private void copyChunk(double buffer[][]) {
			for (int i = 0; i < buffer.length; ++i) {
				buffer[i] = Arrays.copyOf(this.currentBuffer[i],
							  buffer[i].length); // TODO
			}

		}

	}

	private class NewArtifactSignalReaderWorkerData implements
		INewArtifactSignalReaderWorkerData {

		private BlockingQueue<double[][]> inputQueue;
		private BlockingQueue<double[][]> outputQueue;
		private MultichannelSampleSource source;
		private NewArtifactConstants constants;

		public NewArtifactSignalReaderWorkerData(
			MultichannelSampleSource source,
			NewArtifactConstants constants,
			BlockingQueue<double[][]> inputQueue,
			BlockingQueue<double[][]> outputQueue) {
			this.source = source;
			this.constants = constants;
			this.inputQueue = inputQueue;
			this.outputQueue = outputQueue;
		}

		@Override
		public double[][] getWritableBuffer() throws InterruptedException {
			return this.inputQueue.take();
		}

		@Override
		public void markBufferAsReady(double[][] buffer)
		throws InterruptedException {
			this.outputQueue.put(buffer);
		}

		@Override
		public void finalizeBuffers() throws InterruptedException {
			this.outputQueue.put(new double[0][]);
		}

		@Override
		public MultichannelSampleSource getSignalSource() {
			return this.source;
		}

		@Override
		public NewArtifactConstants getArtifactConstants() {
			return this.constants;
		}
	}

	private class TrackerUpdater extends TimerTask {

		final private IPluginComputationMgrStepTrackerProxy<NewArtifactProgressPhase> tracker;
		volatile private int current;
		volatile private int last;
		public final Integer blockCount;

		public TrackerUpdater(
			final IPluginComputationMgrStepTrackerProxy<NewArtifactProgressPhase> tracker,
			int blockCount) {
			this.tracker = tracker;
			this.current = 0;
			this.last = 0;
			this.blockCount = new Integer(blockCount);
		}

		public void setCurrent(int current) {
			synchronized (this) {
				this.current = current;
			}
		}

		@Override
		public void run() {
			int value;
			synchronized (this) {
				value = this.current;
			}

			tracker.advance(value - this.last);
			tracker.setProgressPhase(
				NewArtifactProgressPhase.INTERMEDIATE_COMPUTATION_PHASE,
				new Integer(value), this.blockCount);
			this.last = value;
		}

		public int getLast() {
			return this.current;
		}
	};

	public NewArtifactMgrPreprocessStep(NewArtifactMgrStepData data) {
		super(data);

		this.freeBuffersQueue = new ArrayBlockingQueue<double[][]>(
			this.INPUT_BUFFER_QUEUE_SIZE);
		this.readyBuffersQueue = new ArrayBlockingQueue<double[][]>(
			this.INPUT_BUFFER_QUEUE_SIZE);

		this.shutdownFlag = new AtomicBoolean(false);

		this.workers = new PluginWorkerSet(this.data.threadFactory);
		this.writers = new LinkedList<INewArtifactAlgorithmWriter>();
		this.timer = new Timer();
	}

	@Override
	public int getStepNumberEstimate() {
		return this.getBlockCount() + 1;
	}

	@Override
	public PluginComputationMgrStepResult doRun(
		PluginComputationMgrStepResult prevStepResult)
	throws ComputationException, PluginToolInterruptedException,
		PluginToolAbortException {
		final IPluginComputationMgrStepTrackerProxy<NewArtifactProgressPhase> tracker = this.data.tracker;

		this.checkAbortState();
		tracker.setProgressPhase(NewArtifactProgressPhase.PREPROCESS_PREPARE_PHASE);

		this.prepareWorkers();
		this.checkAbortState();
		tracker.advance(1);

		int current = 0;

		this.timer.scheduleAtFixedRate(this.updater, new Date(), 1500);

		this.workers.startAll();

		this.checkAbortState();
		tracker.setProgressPhase(NewArtifactProgressPhase.SOURCE_FILE_INITIAL_READ_PHASE);

		double buffer[][] = null;
		do {
			try {
				buffer = this.readyBuffersQueue.take();
			} catch (InterruptedException e) {
				throw new PluginToolInterruptedException(e);
			}
			this.synchronizer.addBuffer(buffer);

			current++;
			if (current % 100 == 0) {
				this.checkAbortState();
			}
			updater.setCurrent(current);
		} while (buffer != null && buffer.length != 0);

		this.checkAbortState();

		return this.prepareStepResult();
	}

	private void prepareWorkers() throws ComputationException {
		int channelCount = this.data.artifactData.getSampleSource()
				   .getChannelCount();
		int blockLength = this.data.constants.getBlockLengthWithPadding();

		double[][][] inputBuffer = new double[this.INPUT_BUFFER_QUEUE_SIZE][channelCount][blockLength];

		for (double[][] buffer : inputBuffer) {
			this.freeBuffersQueue.add(buffer);
		}

		this.synchronizer = new NewArtifactAlgorithmBufferSynchronizer(
			this.freeBuffersQueue, this.shutdownFlag);

		NewArtifactSignalReaderWorker reader = new NewArtifactSignalReaderWorker(
			new NewArtifactSignalReaderWorkerData(
				this.data.artifactData.getSampleSource(),
				this.data.constants, this.freeBuffersQueue,
				this.readyBuffersQueue));

		for (NewArtifactComputationType algorithmType : NewArtifactComputationType
				.values()) {
			INewArtifactAlgorithmWriter writer = this
							     .createResultWriterForAlgorithm(algorithmType);
			if (writer != null) {
				this.writers.add(writer);
				this.workers.add(new NewArtifactAlgorithmWorker(
							 new NewArtifactAlgorithmDataSource(synchronizer),
							 new NewArtifactAlgorithmFactory(algorithmType,
									 this.data.constants), writer,
							 new NewArtifactAlgorithmWorkerData(
								 this.data.artifactData, this.data.constants)));
			}
		}
		this.workers.add(reader);

		this.updater = new TrackerUpdater(this.data.tracker,
						  this.getBlockCount());
	}

	@Override
	protected NewArtifactMgrStepResult prepareStepResult() {
		return new NewArtifactMgrStepResult(this.getClass());
	}

	@Override
	protected void cleanup() {
		super.cleanup();
		this.stopWorkers();
	}

	private void stopWorkers() {
		try {
			if (this.synchronizer != null) {
				this.synchronizer.addBuffer(null);
			}

			this.shutdownFlag.set(true);

			this.freeBuffersQueue.clear();
			this.freeBuffersQueue.add(new double[0][]);

			this.timer.cancel();

			this.data.tracker.setProgressPhase(
				NewArtifactProgressPhase.INTERMEDIATE_COMPUTATION_PHASE,
				this.updater.blockCount, this.updater.blockCount);
			int advance = this.updater.blockCount - this.updater.getLast() + 1;
			if (advance > 0) {
				this.data.tracker.advance(advance);
			}
		} finally {
			this.terminateWorkers();
		}
	}

	private void terminateWorkers() {
		this.workers.terminateAll();

		for (INewArtifactAlgorithmWriter writer : this.writers) {
			try {
				writer.close();
			} catch (IOException e) {

			}
		}
		this.writers.clear();
	}

	private INewArtifactAlgorithmWriter createResultWriterForAlgorithm(
		NewArtifactComputationType algorithmType)
	throws ComputationException {
		if (!NewArtifactParameterHelper.IsParameterEnabled(algorithmType,
				this.data.artifactData.getParameters())) {
			return null;
		}

		String fileNames[] = this.data.pathConstructor
				     .getIntermediateFileNamesForAlgorithm(algorithmType);
		String workDirName = this.data.pathConstructor.getPathToWorkDir();

		try {
			switch (algorithmType) {
			case MUSCLE_PLUS_POWER:
				assert fileNames.length == 2;
				return new NewArtifactDoubleFileAlgorithmWriter(new File(
							workDirName, fileNames[0]), new File(workDirName,
									fileNames[1]));
			case MUSCLE_ACTIVITY:
			case POWER:
				return null;
			case EYE_MOVEMENT:
			case TECHNICAL:
			case ECG:
			case EYEBLINKS:
			case UNKNOWN:
			default:
				assert fileNames.length == 1;
				return new NewArtifactAlgorithmWriter(new File(workDirName,
								      fileNames[0]));
			}
		} catch (SignalMLException e) {
			throw new ComputationException(e);
		}
	}

	private int getBlockCount() {
		return PluginSignalHelper.GetBlockCount(
			       this.data.artifactData.getSampleSource(),
			       this.data.constants.getBlockLength());
	}

}
