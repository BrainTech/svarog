package org.signalml.plugin.newstager.data;

import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.plugin.newstager.io.INewStagerStatsSynchronizer;

public class NewStagerSignalReaderWorkerData {
	public final MultichannelSampleSource sampleSource;
	public final INewStagerStatsSynchronizer synchronizer;

	public NewStagerSignalReaderWorkerData(MultichannelSampleSource sampleSource,
					       INewStagerStatsSynchronizer synchronizer) {
		this.sampleSource = sampleSource;
		this.synchronizer = synchronizer;
	}
}
