package org.signalml.plugin.newstager.logic.mgr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import org.signalml.method.ComputationException;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.exception.PluginToolAbortException;
import org.signalml.plugin.exception.PluginToolInterruptedException;
import org.signalml.plugin.method.logic.AbstractPluginComputationMgrStep;
import org.signalml.plugin.method.logic.PluginWorkerSet;
import org.signalml.plugin.newstager.data.NewStagerBookInfo;
import org.signalml.plugin.newstager.data.NewStagerBookReaderWorkerData;
import org.signalml.plugin.newstager.data.NewStagerResult;
import org.signalml.plugin.newstager.data.NewStagerSignalStatsResult;
import org.signalml.plugin.newstager.data.logic.INewStagerWorkerCompletion;
import org.signalml.plugin.newstager.data.logic.NewStagerBookProcessorResult;
import org.signalml.plugin.newstager.data.logic.NewStagerBookProcessorStepResult;
import org.signalml.plugin.newstager.data.logic.NewStagerBookProcessorWorkerData;
import org.signalml.plugin.newstager.data.logic.NewStagerMgrStepData;
import org.signalml.plugin.newstager.io.NewStagerBookReaderWorker;
import org.signalml.plugin.newstager.logic.book.NewStagerBookDataProvider;
import org.signalml.plugin.newstager.logic.book.NewStagerBookProcessorWorker;

public class NewStagerBookProcessStep extends
	AbstractPluginComputationMgrStep<NewStagerMgrStepData> {

	private NewStagerSignalStatsResult signalStatResult;

	private PluginWorkerSet workers;
	private NewStagerBookDataProvider bookDataProvider;
	private AtomicInteger aliveWorkers;

	private Collection<NewStagerBookProcessorResult> partialResults;

	public NewStagerBookProcessStep(NewStagerMgrStepData data) {
		super(data);

		this.signalStatResult = null;

		this.workers = new PluginWorkerSet(this.data.threadFactory);
		this.bookDataProvider = new NewStagerBookDataProvider();

		this.partialResults = null;

		this.aliveWorkers = null;
	}

	@Override
	public int getStepNumberEstimate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void doInitialize() {
		NewStagerBookReaderWorker bookReaderWorker = new NewStagerBookReaderWorker(
			new NewStagerBookReaderWorkerData(
				this.data.parameters.bookFilePath,
				this.bookDataProvider));
		this.workers.submit(bookReaderWorker);
	}

	@Override
	protected NewStagerResult prepareStepResult() {
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

		this.checkAbortState();
		this.prepareWorkers();

		this.checkAbortState();
		this.workers.startAll();
		this.checkAbortState();

		waitForWorkerCompletion();

		return this.prepareStepResult();
	}

	@Override
	protected void cleanup() {
		super.cleanup();
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
		final Collection<NewStagerBookProcessorResult> partialResults = this.partialResults = new ArrayList<NewStagerBookProcessorResult>(
			numberOfThreads);

		INewStagerWorkerCompletion<NewStagerBookProcessorResult> completion = new INewStagerWorkerCompletion<NewStagerBookProcessorResult>() {

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
								 this.data.fixedParameters,
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
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new PluginToolInterruptedException(e);
			}

			this.checkAbortState();
		}
	}

}
