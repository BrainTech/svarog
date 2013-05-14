package org.signalml.plugin.newstager.logic.mgr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import org.signalml.method.ComputationException;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.exception.PluginToolAbortException;
import org.signalml.plugin.exception.PluginToolInterruptedException;
import org.signalml.plugin.method.helper.AbstractPluginTrackerUpdaterWithTimer;
import org.signalml.plugin.method.logic.AbstractPluginComputationMgrStep;
import org.signalml.plugin.method.logic.IPluginComputationMgrStepTrackerProxy;
import org.signalml.plugin.method.logic.PluginWorkerSet;
import org.signalml.plugin.newstager.data.NewStagerBookInfo;
import org.signalml.plugin.newstager.data.NewStagerBookReaderWorkerData;
import org.signalml.plugin.newstager.data.NewStagerSignalStatsResult;
import org.signalml.plugin.newstager.data.logic.INewStagerWorkerCompletion;
import org.signalml.plugin.newstager.data.logic.NewStagerBookProcessorResult;
import org.signalml.plugin.newstager.data.logic.NewStagerBookProcessorStepResult;
import org.signalml.plugin.newstager.data.logic.NewStagerBookProcessorWorkerData;
import org.signalml.plugin.newstager.data.logic.NewStagerComputationProgressPhase;
import org.signalml.plugin.newstager.data.logic.NewStagerMgrStepData;
import org.signalml.plugin.newstager.io.NewStagerBookReaderWorker;
import org.signalml.plugin.newstager.logic.book.NewStagerBookDataProvider;
import org.signalml.plugin.newstager.logic.book.NewStagerBookProcessorWorker;
import org.signalml.plugin.signal.PluginSignalHelper;

public class NewStagerBookProcessStep extends
	AbstractPluginComputationMgrStep<NewStagerMgrStepData> {

	private class TrackerUpdater extends AbstractPluginTrackerUpdaterWithTimer {

		private final NewStagerBookProcessStep step;
		private final IPluginComputationMgrStepTrackerProxy<NewStagerComputationProgressPhase> tracker;

		private int segmentCount;

		public TrackerUpdater(
			NewStagerBookProcessStep step,
			IPluginComputationMgrStepTrackerProxy<NewStagerComputationProgressPhase> tracker) {
			this.step = step;
			this.tracker = tracker;
		}

		@Override
		public void update(int progress, int prevProgress) {
			int tick = Math.max(0, progress - prevProgress);
			if (tick > 0) {
				int segmentCount;
				synchronized (this) {
					segmentCount = this.segmentCount;
				}
				this.tracker.advance(this.step, tick);
				this.tracker.setProgressPhase(NewStagerComputationProgressPhase.BOOK_PROCESSING_PHASE, progress, segmentCount);
			}
		}

		public void setSegmentCount(int segmentCount) {
			synchronized (this) {
				this.segmentCount = segmentCount;
			}
		}

	}

	private final PluginWorkerSet workers;
	private final NewStagerBookDataProvider bookDataProvider;
	private final TrackerUpdater trackerUpdater;

	private boolean hasExactSegmentCount;

	private AtomicInteger aliveWorkers;

	private Collection<NewStagerBookProcessorResult> partialResults;

	private NewStagerSignalStatsResult signalStatResult;

	public NewStagerBookProcessStep(NewStagerMgrStepData data) {
		super(data);

		this.hasExactSegmentCount = false;

		this.signalStatResult = null;

		this.workers = new PluginWorkerSet(this.data.threadFactory);
		this.bookDataProvider = new NewStagerBookDataProvider();

		this.trackerUpdater = new TrackerUpdater(this, this.data.tracker);

		this.partialResults = null;

		this.aliveWorkers = null;
	}

	@Override
	public int getStepNumberEstimate() {
		return this.getAtomTickerEstimate(this.getSegmentCount());
	}

	@Override
	protected void doInitialize() {
		NewStagerBookReaderWorker bookReaderWorker = new NewStagerBookReaderWorker(
			new NewStagerBookReaderWorkerData(
				this.data.stagerData.getParameters().bookFilePath,
				this.bookDataProvider));
		this.workers.submit(bookReaderWorker);
	}

	@Override
	protected NewStagerBookProcessorStepResult prepareStepResult() {
		if (this.partialResults == null || this.signalStatResult == null) {
			return null;
		}

		NewStagerBookInfo bookInfo;
		try {
			bookInfo = this.bookDataProvider.getBookInfo();
		} catch (InterruptedException e) {
			// TODO log exception
			return null;
		}

		if (bookInfo == null) {
			return null;
		}

		return new NewStagerBookProcessorStepResult(bookInfo,
				this.partialResults,
				this.signalStatResult.montage);
	}

	@Override
	protected PluginComputationMgrStepResult doRun(
		PluginComputationMgrStepResult prevStepResult)
	throws PluginToolAbortException, PluginToolInterruptedException,
		ComputationException {

		try {
			this.signalStatResult = (NewStagerSignalStatsResult) prevStepResult;
		} catch (ClassCastException e) {
			throw new ComputationException(e);
		}

		this.data.tracker.setProgressPhase(NewStagerComputationProgressPhase.BOOK_FILE_INITIAL_READ_PHASE);

		this.checkAbortState();
		this.prepareWorkers();

		this.trackerUpdater.setSegmentCount(this.getSegmentCount());
		this.trackerUpdater.start(100);

		this.checkAbortState();
		this.workers.startAll();
		this.checkAbortState();

		this.waitForWorkerCompletion();

		return this.prepareStepResult();
	}

	@Override
	protected void cleanup() {
		super.cleanup();
		this.trackerUpdater.stop();
		this.stopWorkers();
	}

	private void prepareWorkers() {
		if (this.signalStatResult == null) {
			return;
		}

		int numberOfThreads = Math.max(1, Runtime.getRuntime()
									   .availableProcessors());

		final AtomicInteger counter = this.aliveWorkers = new AtomicInteger(
			numberOfThreads);
		final AtomicInteger processedAtomCount = new AtomicInteger();
		final Collection<NewStagerBookProcessorResult> partialResults = this.partialResults = new ArrayList<NewStagerBookProcessorResult>(
			numberOfThreads);

		INewStagerWorkerCompletion<NewStagerBookProcessorResult> completion = new INewStagerWorkerCompletion<NewStagerBookProcessorResult>() {

			@Override
			public void signalProgress(int i) {
				trackerUpdater.setProgress(processedAtomCount.addAndGet(i));
			}

			@Override
			public void completeWork(NewStagerBookProcessorResult result) {
				try {
					synchronized (partialResults) {
						partialResults.add(result);
					}
				} finally {
					counter.decrementAndGet();
				}
			}

		};

		for (int i = 0; i < numberOfThreads; ++i) {
			this.workers.add(new NewStagerBookProcessorWorker(
								 new NewStagerBookProcessorWorkerData(this.bookDataProvider,
										 completion, this.data.constants,
										 this.data.stagerData.getChannelMap(),
										 this.signalStatResult.newParameters,
										 this.data.stagerData.getFixedParameters(),
										 this.signalStatResult.muscle,
										 this.signalStatResult.signalStatCoeffs)));
		}

	}

	private void stopWorkers() {
		this.bookDataProvider.shutdown();
		this.workers.terminateAll();
	}

	private void waitForWorkerCompletion()
	throws PluginToolInterruptedException, PluginToolAbortException {
		while (this.aliveWorkers.get() != 0) {
			this.updateSegmentCountIfNeeded();

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new PluginToolInterruptedException(e);
			}

			this.checkAbortState();
		}
	}

	private void updateSegmentCountIfNeeded() {
		if (this.hasExactSegmentCount) {
			return;
		}

		NewStagerBookInfo bookInfo = this.bookDataProvider.tryGetBookInfo();
		if (bookInfo == null) {
			return;
		}

		this.trackerUpdater.setSegmentCount(bookInfo.segmentCount);
		this.data.tracker.setTickerLimit(this, this.getAtomTickerEstimate(bookInfo.segmentCount));

		this.hasExactSegmentCount = true;
	}

	private int getSegmentCount() {
		int segmentCount;

		NewStagerBookInfo bookInfo = this.bookDataProvider.tryGetBookInfo();

		if (bookInfo != null) {
			segmentCount = bookInfo.segmentCount;
			this.hasExactSegmentCount = true;
		} else {
			//very rough estimate of number of atom segments
			segmentCount = PluginSignalHelper.GetBlockCount(
							   this.data.stagerData.getSampleSource(),
							   this.data.constants.getBlockLengthInSamples());
		}

		return segmentCount;
	}

	private int getAtomTickerEstimate(int segmentCount) {
		int ratio = this.data.constants.getBlockLengthInSamples() / 20;
		return (int) Math.ceil(((double) segmentCount) / ratio);
	}

}
