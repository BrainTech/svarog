package org.signalml.plugin.newstager.logic.artifact;

import org.signalml.plugin.exception.PluginThreadRuntimeException;
import org.signalml.plugin.newstager.data.NewStagerStatWorkerData;
import org.signalml.plugin.newstager.data.logic.INewStagerWorkerCompletion;
import org.signalml.plugin.newstager.data.logic.NewStagerStatAlgorithmResult;
import org.signalml.plugin.newstager.data.logic.NewStagerStatData;
import org.signalml.plugin.newstager.exception.NewStagerPluginException;
import org.signalml.plugin.newstager.io.INewStagerStatsSynchronizer;

public class NewStagerStatWorker implements Runnable {

	private final NewStagerStatWorkerData data;

	public NewStagerStatWorker(NewStagerStatWorkerData data) {
		this.data = data;
	}

	@Override
	public void run() {
		INewStagerStatsSynchronizer synchronizer = this.data.synchronizer;
		INewStagerWorkerCompletion<NewStagerStatAlgorithmResult> completion = this.data.completion;

		try {
			NewStagerStatData data = new NewStagerStatData(this.data.constants,
					this.data.parameters, this.data.channelMap);
			NewStagerStatAlgorithm stagerStat = new NewStagerStatAlgorithm(data);

			int count = 0;
			int loopCount = this.data.constants.segmentCount;
			while (true) {
				double buffer[][] = synchronizer.getReadyBuffer();
				if (buffer == null) {
					break;
				}

				if (count < loopCount) {
					stagerStat.compute(buffer);
					++count;
				}

				synchronizer.markBufferAsProcessed(buffer);
				completion.signalProgress(1);
			}

			completion.completeWork(stagerStat.getResult());
		} catch (InterruptedException e) {
			completion.completeWork(null);
			return;
		} catch (NewStagerPluginException e) {
			this.data.completion.completeWork(null);
			throw new PluginThreadRuntimeException(e);
		}

	}

}
