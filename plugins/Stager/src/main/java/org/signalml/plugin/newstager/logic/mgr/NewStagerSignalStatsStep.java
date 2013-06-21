package org.signalml.plugin.newstager.logic.mgr;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.method.ComputationException;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.exception.PluginToolAbortException;
import org.signalml.plugin.exception.PluginToolInterruptedException;
import org.signalml.plugin.method.helper.AbstractPluginTrackerUpdaterWithTimer;
import org.signalml.plugin.method.logic.AbstractPluginComputationMgrStep;
import org.signalml.plugin.method.logic.IPluginComputationMgrStepTrackerProxy;
import org.signalml.plugin.method.logic.PluginWorkerSet;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerFASPThreshold;
import org.signalml.plugin.newstager.data.NewStagerParameterThresholds;
import org.signalml.plugin.newstager.data.NewStagerParameters;
import org.signalml.plugin.newstager.data.NewStagerSignalReaderWorkerData;
import org.signalml.plugin.newstager.data.NewStagerSignalStatsResult;
import org.signalml.plugin.newstager.data.NewStagerSleepStats;
import org.signalml.plugin.newstager.data.NewStagerStatWorkerData;
import org.signalml.plugin.newstager.data.logic.INewStagerWorkerCompletion;
import org.signalml.plugin.newstager.data.logic.NewStagerComputationProgressPhase;
import org.signalml.plugin.newstager.data.logic.NewStagerMgrStepData;
import org.signalml.plugin.newstager.data.logic.NewStagerStatAlgorithmResult;
import org.signalml.plugin.newstager.io.INewStagerStatsSynchronizer;
import org.signalml.plugin.newstager.io.NewStagerSignalReaderWorker;
import org.signalml.plugin.newstager.logic.artifact.NewStagerStatWorker;
import org.signalml.plugin.signal.PluginSignalHelper;
import org.signalml.util.MinMaxRange;

public class NewStagerSignalStatsStep extends
	AbstractPluginComputationMgrStep<NewStagerMgrStepData> {

	private class TrackerUpdater extends AbstractPluginTrackerUpdaterWithTimer {

		private final NewStagerSignalStatsStep step;
		private final IPluginComputationMgrStepTrackerProxy<NewStagerComputationProgressPhase> tracker;
		private final int blockCount;

		public TrackerUpdater(
			NewStagerSignalStatsStep step,
			IPluginComputationMgrStepTrackerProxy<NewStagerComputationProgressPhase> tracker,
			int blockCount) {
			this.step = step;
			this.tracker = tracker;
			this.blockCount = blockCount;
		}

		@Override
		public void update(int progress, int prevProgress) {
			if (progress < this.blockCount) {
				this.tracker.advance(this.step,
									 Math.max(progress - prevProgress, 0));
				this.tracker
				.setProgressPhase(
					NewStagerComputationProgressPhase.SIGNAL_STATS_BLOCK_COMPUTATION_PHASE,
					progress, this.blockCount);
			}
		}

	}

	private static final int BUFFER_QUEUE_SIZE = 20;

	private final PluginWorkerSet workers;

	private final TrackerUpdater trackerUpdater;

	private final AtomicInteger progressBlockCount;
	private final AtomicBoolean statResultReadyFlag;
	
	private final int blockLengthInSapmles;

	private Integer blockCount;

	private NewStagerStatAlgorithmResult statResult;

	public NewStagerSignalStatsStep(NewStagerMgrStepData data) {
		super(data);

		this.workers = new PluginWorkerSet(this.data.threadFactory);
		this.progressBlockCount = new AtomicInteger(0);
		this.statResultReadyFlag = new AtomicBoolean(false);

		this.trackerUpdater = new TrackerUpdater(this, this.data.tracker,
				this.getBlockCount());

		this.statResult = null;
		
		this.blockLengthInSapmles = this.data.constants.getBlockLengthInSamples();
	}

	@Override
	public int getStepNumberEstimate() {
		return this.getBlockCount() + 1;
	}

	@Override
	protected PluginComputationMgrStepResult prepareStepResult() {
		if (!this.statResultReadyFlag.get()) {
			return null;
		}

		NewStagerParameters parameters = this.computeNewParameters();

		return new NewStagerSignalStatsResult(
				   this.getSignalStatCoeffs(parameters), parameters,
				   this.statResult.muscle, this.statResult.montage);
	}

	@Override
	protected PluginComputationMgrStepResult doRun(
		PluginComputationMgrStepResult prevStepResult)
	throws PluginToolAbortException, PluginToolInterruptedException,
		ComputationException {

		IPluginComputationMgrStepTrackerProxy<NewStagerComputationProgressPhase> tracker = this.data.tracker;

		tracker.setProgressPhase(NewStagerComputationProgressPhase.SIGNAL_STATS_PREPARE_PHASE);

		this.createWorkers();

		this.workers.startAll();

		tracker.setProgressPhase(NewStagerComputationProgressPhase.SIGNAL_STATS_SOURCE_FILE_INITIAL_READ_PHASE);

		this.trackerUpdater.start(1500);

		while (!this.statResultReadyFlag.get()) {
			try {
				Thread.sleep(500);
				this.trackerUpdater.setProgress(this.progressBlockCount.get());
			} catch (InterruptedException e) {
				throw new PluginToolInterruptedException(e);
			}
			this.checkAbortState();
		}

		return this.prepareStepResult();
	}

	@Override
	protected void cleanup() {
		super.cleanup();
		this.trackerUpdater.stop();
		this.stopWorkers();
	}

	private void createWorkers() {
		MultichannelSampleSource source = this.data.stagerData
										  .getSampleSource();

		final BlockingQueue<double[][]> freeBufferQueue = new ArrayBlockingQueue<double[][]>(
			BUFFER_QUEUE_SIZE);
		final BlockingQueue<double[][]> readyBufferQueue = new ArrayBlockingQueue<double[][]>(
			BUFFER_QUEUE_SIZE);

		for (int i = 0; i < BUFFER_QUEUE_SIZE; ++i) {
			freeBufferQueue
			.add(new double[source.getChannelCount()][this.data.constants
					.getBlockLengthInSamples()]);
		}

		INewStagerStatsSynchronizer synchronizer = new INewStagerStatsSynchronizer() {

			AtomicBoolean shutdownFlag = new AtomicBoolean();

			@Override
			public void markBufferAsReady(double[][] buffer)
			throws InterruptedException {
				readyBufferQueue.add(buffer);
			}

			@Override
			public double[][] getWritableBuffer() throws InterruptedException {
				return freeBufferQueue.take();
			}

			@Override
			public double[][] getReadyBuffer() throws InterruptedException {
				double result[][] = null;
				do {
					result = readyBufferQueue.poll(1000, TimeUnit.MILLISECONDS);
					if (result == null && this.shutdownFlag.get()) {
						return null;
					}
				} while (result == null);
				return result;
			}

			@Override
			public void markBufferAsProcessed(double buffer[][])
			throws InterruptedException {
				freeBufferQueue.add(buffer);
			}

			@Override
			public void finalizeBuffers() throws InterruptedException {
				this.shutdownFlag.set(true);
			}

			@Override
			public int getBufferLength() {
				return blockLengthInSapmles;
			}
		};

		final AtomicBoolean resultReadyFlag = this.statResultReadyFlag;
		INewStagerWorkerCompletion<NewStagerStatAlgorithmResult> completion = new INewStagerWorkerCompletion<NewStagerStatAlgorithmResult>() {

			@Override
			public void signalProgress(int i) {
				progressBlockCount.incrementAndGet();
			}

			@Override
			public void completeWork(NewStagerStatAlgorithmResult result) {
				resultReadyFlag.set(true);
				statResult = result;
			}

		};

		Runnable readerWorker = new NewStagerSignalReaderWorker(
			new NewStagerSignalReaderWorkerData(source, synchronizer));
		Runnable statWorker = new NewStagerStatWorker(
			new NewStagerStatWorkerData(synchronizer, completion,
										this.data.constants,
										this.data.stagerData.getParameters(),
										this.data.stagerData.getChannelMap()));

		this.workers.add(readerWorker);
		this.workers.add(statWorker);
	}

	private void stopWorkers() {
		this.workers.terminateAll();
	}

	private NewStagerParameters computeNewParameters() {
		NewStagerParameters oldParameters = this.data.stagerData
											.getParameters();
		if (!this.statResultReadyFlag.get()) {
			return oldParameters;
		}

		NewStagerParameterThresholds thresholds = oldParameters.thresholds;
		NewStagerConstants constants = this.data.constants;

		double coeff = constants.amplitudeA * this.statResult.deviation
					   + constants.amplitudeB;

		return new NewStagerParameters(oldParameters.bookFilePath,
									   oldParameters.rules, oldParameters.analyseEMGChannelFlag,
									   oldParameters.analyseEEGChannelsFlag,
									   oldParameters.primaryHypnogramFlag,
									   new NewStagerParameterThresholds(
										   this.computeMontageToneEMGThreshold(),
										   thresholds.montageEEGThreshold,
										   thresholds.montageEMGThreshold,
										   thresholds.montageToneEMGThreshold,
										   thresholds.remEogDeflectionThreshold,
										   thresholds.semEogDeflectionThreshold,
										   this.convertThreshold(thresholds.alphaThreshold, coeff,
												   constants.alphaOffset), this.convertThreshold(
											   thresholds.deltaThreshold, coeff,
											   constants.deltaOffset), this.convertThreshold(
											   thresholds.spindleThreshold, coeff,
											   constants.spindleOffset),
										   thresholds.thetaThreshold, thresholds.kCThreshold));
	}

	private NewStagerFASPThreshold convertThreshold(
		NewStagerFASPThreshold threshold, double coeff, double offset) {
		if (threshold.amplitude.getMin() == MinMaxRange.AUTO) {
			MinMaxRange amplitude = threshold.amplitude;
			return new NewStagerFASPThreshold(new MinMaxRange(
												  amplitude.getUnlimitedValue(), coeff - offset,
												  amplitude.getMax(), amplitude.isMinUnlimited(),
												  amplitude.isMaxUnlimited()), threshold.frequency,
											  threshold.scale, threshold.phase);
		}
		return threshold;
	}

	private double computeMontageToneEMGThreshold() {
		Double oldThreshold = this.data.stagerData.getParameters().thresholds.toneEMG;
		if (oldThreshold.isNaN()) {
			double t[] = this.statResult.muscle;
			double mean = 0.0d;

			int length = Math.max(t.length - 1, 0); //for some reason we discard the last element

			for (int i = 0; i < length; ++i) {
				mean += t[i];
			}
			mean /= t.length;	// this is not the actual mean
			return (mean > this.data.constants.muscleThreshold) ? this.data.constants.muscleThreshold
				   : mean / this.data.constants.muscleThresholdRate;
		} else {
			return oldThreshold;
		}
	}

	private NewStagerSleepStats getSignalStatCoeffs(
		NewStagerParameters parameters) {
		NewStagerParameterThresholds thresholds = parameters.thresholds;
		return new NewStagerSleepStats(
				   thresholds.alphaThreshold.amplitude.getMin(),
				   thresholds.deltaThreshold.amplitude.getMin(),
				   thresholds.spindleThreshold.amplitude.getMin(),
				   thresholds.toneEMG);
	}

	private int getBlockCount() {
		if (this.blockCount == null) {
			this.blockCount = new Integer(PluginSignalHelper.GetBlockCount(
											  this.data.stagerData.getSampleSource(),
											  this.data.constants.getBlockLengthInSamples()));
		}
		return this.blockCount;
	}

}
