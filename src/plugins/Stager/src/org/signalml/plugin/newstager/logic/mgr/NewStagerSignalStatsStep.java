package org.signalml.plugin.newstager.logic.mgr;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.method.ComputationException;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.exception.PluginToolAbortException;
import org.signalml.plugin.exception.PluginToolInterruptedException;
import org.signalml.plugin.method.logic.AbstractPluginComputationMgrStep;
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
import org.signalml.plugin.newstager.data.logic.NewStagerMgrStepData;
import org.signalml.plugin.newstager.data.logic.NewStagerStatAlgorithmResult;
import org.signalml.plugin.newstager.io.INewStagerStatsSynchronizer;
import org.signalml.plugin.newstager.io.NewStagerSignalReaderWorker;
import org.signalml.plugin.newstager.logic.artifact.NewStagerStatWorker;
import org.signalml.util.MinMaxRange;

public class NewStagerSignalStatsStep extends
	AbstractPluginComputationMgrStep<NewStagerMgrStepData> {

	private static final int BUFFER_QUEUE_SIZE = 20;

	private PluginWorkerSet workers;

	private AtomicBoolean statResultReadyFlag;
	private NewStagerStatAlgorithmResult statResult;

	public NewStagerSignalStatsStep(NewStagerMgrStepData data) {
		super(data);

		this.workers = new PluginWorkerSet(this.data.threadFactory);
		this.statResultReadyFlag = new AtomicBoolean(false);
		this.statResult = null;
	}

	@Override
	public int getStepNumberEstimate() {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected PluginComputationMgrStepResult prepareStepResult() {
		if (!this.statResultReadyFlag.get()) {
			return null;
		}

		NewStagerParameters parameters = this.computeNewParamters();

		return new NewStagerSignalStatsResult(
			       this.getSignalStatCoeffs(parameters), parameters,
			       this.statResult.muscle, this.statResult.montage);
	}

	@Override
	protected PluginComputationMgrStepResult doRun(
		PluginComputationMgrStepResult prevStepResult)
	throws PluginToolAbortException, PluginToolInterruptedException,
		ComputationException {

		this.createWorkers();

		this.workers.startAll();

		while (!this.statResultReadyFlag.get()) {
			try {
				Thread.sleep(1000);
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
					.getBlockLength()]);
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
		};

		final AtomicBoolean resultReadyFlag = this.statResultReadyFlag;
		INewStagerWorkerCompletion<NewStagerStatAlgorithmResult> completion = new INewStagerWorkerCompletion<NewStagerStatAlgorithmResult>() {

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
						    this.data.constants, this.data.parameters,
						    this.data.stagerData.getChannelMap()));

		this.workers.add(readerWorker);
		this.workers.add(statWorker);
	}

	private void stopWorkers() {
		this.workers.terminateAll();
	}

	private NewStagerParameters computeNewParamters() {
		NewStagerParameters oldParameters = this.data.parameters;
		if (!this.statResultReadyFlag.get()) {
			return oldParameters;
		}

		NewStagerParameterThresholds thresholds = oldParameters.thresholds;
		NewStagerConstants constants = this.data.constants;

		double coeff = constants.amplitudeA * this.statResult.deviation
			       + constants.amplitudeB;

		return new NewStagerParameters(oldParameters.bookFilePath,
					       oldParameters.segmentCount,
					       oldParameters.analyseEMGChannelFlag,
					       oldParameters.analyseEEGChannelsFlag,
					       oldParameters.primaryHypnogramFlag,
					       new NewStagerParameterThresholds(
						       this.computeMontageToneEMGThreshold(),
						       thresholds.montageEEGThreshold,
						       thresholds.montageEMGThreshold,
						       thresholds.montageToneEMGThreshold,
						       this.convertThreshold(thresholds.alphaThreshold, coeff,
								       constants.alphaOffset), this.convertThreshold(
										       thresholds.deltaThreshold, coeff, constants.deltaOffset),
						       this.convertThreshold(thresholds.spindleThreshold,
								       coeff, constants.spindleOffset),
						       thresholds.thetaThreshold,
						       thresholds.kCThreshold));
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

	private Double computeMontageToneEMGThreshold() {
		Double oldThreshold = this.data.parameters.thresholds.toneEMG;
		if (oldThreshold == null) {
			double t[] = this.statResult.muscle;
			double mean = 0.0d;
			for (int i = 0; i < t.length; ++i) {
				mean += t[i];
			}
			mean /= t.length;
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

}
