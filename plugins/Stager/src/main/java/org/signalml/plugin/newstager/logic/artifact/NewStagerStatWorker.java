package org.signalml.plugin.newstager.logic.artifact;

import org.signalml.plugin.exception.PluginThreadRuntimeException;
import org.signalml.plugin.newstager.data.NewStagerStatWorkerData;
import org.signalml.plugin.newstager.data.logic.NewStagerStatData;
import org.signalml.plugin.newstager.exception.NewStagerPluginException;
import org.signalml.plugin.newstager.io.INewStagerStatsSynchronizer;

public class NewStagerStatWorker implements Runnable {

	private NewStagerStatWorkerData data;

	public NewStagerStatWorker(NewStagerStatWorkerData data) {
		this.data = data;
	}

	@Override
	public void run() {
		try {
			NewStagerStatData data = new NewStagerStatData(this.data.constants,
					this.data.parameters, this.data.channelMap);
			NewStagerStatAlgorithm stagerStat = new NewStagerStatAlgorithm(data);

			INewStagerStatsSynchronizer synchronizer = this.data.synchronizer;
			
			int count = 0;
			int channelCount = 0, sampleCount = 0;
			while (true) { // TODO
				double buffer[][] = synchronizer.getReadyBuffer();
				if (buffer == null) {
					break;
				}
				channelCount = buffer.length;
				sampleCount = buffer[0].length;
				
				stagerStat.compute(buffer);
				synchronizer.markBufferAsProcessed(buffer);
				++count;
			}
			
			double zeroedBuffer[][] = new double[channelCount][sampleCount];
			while (count < this.data.constants.segmentCount) {
				stagerStat.compute(zeroedBuffer);
				++count;
			}
			
			this.data.completion.completeWork(stagerStat.getResult());
		} catch (InterruptedException e) {
			this.data.completion.completeWork(null);
			return;
		} catch (NewStagerPluginException e) {
			this.data.completion.completeWork(null);
			throw new PluginThreadRuntimeException(e);
		}

	}

}
