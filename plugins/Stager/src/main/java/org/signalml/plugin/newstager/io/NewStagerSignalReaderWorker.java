package org.signalml.plugin.newstager.io;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.plugin.io.IPluginDataSourceReader;
import org.signalml.plugin.newstager.data.NewStagerSignalReaderWorkerData;

public class NewStagerSignalReaderWorker implements Runnable {

	private final NewStagerSignalReaderWorkerData data;

	public NewStagerSignalReaderWorker(NewStagerSignalReaderWorkerData data) {
		this.data = data;
	}

	@Override
	public void run() {
		MultichannelSampleSource source = this.data.sampleSource;
		IPluginDataSourceReader reader = new NewStagerSignalReader(source);
		INewStagerStatsSynchronizer synchronizer = this.data.synchronizer;

		try {
			while (reader.hasMoreSamples()) {
				double buffer[][] = synchronizer.getWritableBuffer();
				if (buffer == null) {
					return;
				}
				reader.getSample(buffer);
				synchronizer.markBufferAsReady(buffer);
			}
		} catch (InterruptedException e) {
			//TODO
		} finally {
			try {
				synchronizer.finalizeBuffers();
			} catch (InterruptedException e) {
				//do nothing
			}
		}

	}

}
