package org.signalml.plugin.newstager.logic.book;

import org.apache.log4j.Logger;
import org.signalml.plugin.newstager.data.NewStagerBookInfo;
import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.book.NewStagerSingleBookProcessorData;
import org.signalml.plugin.newstager.data.logic.NewStagerBookProcessorWorkerData;
import org.signalml.plugin.newstager.data.tag.NewStagerBookProcessorData;

public class NewStagerBookProcessorWorker implements Runnable {

	protected static final Logger logger = Logger
			.getLogger(NewStagerBookProcessorWorker.class);

	private final NewStagerBookProcessorWorkerData data;

	public NewStagerBookProcessorWorker(NewStagerBookProcessorWorkerData data) {
		this.data = data;
	}

	@Override
	public void run() {
		final NewStagerBookDataProvider provider = this.data.provider;
		NewStagerBookSample sample;

		try {
			NewStagerBookInfo bookInfo = provider.getBookInfo();
			NewStagerSingleBookProcessor processor = new NewStagerSingleBookProcessor(
					new NewStagerBookProcessorData(this.data.constants,
							this.data.channelMap,
							this.data.parameters, this.data.fixedParameters,
							this.data.muscle, this.data.signalStatCoeffs,
							bookInfo));
			while (true) {
				sample = provider.getNextBookAtoms();
				if (sample == null) {
					break;
				}

				processor.process(new NewStagerSingleBookProcessorData(sample));
				
				this.data.completion.signalProgress(1);
			}

			this.data.completion.completeWork(processor.getResult());
		} catch (InterruptedException e) {
			logger.warn("Worker thread interrupted", e);
			return;
		}
	}

}
