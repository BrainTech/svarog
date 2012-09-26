package org.signalml.plugin.newartifact.data;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

public interface INewArtifactSignalReaderWorkerData {
	double[][] getWritableBuffer() throws InterruptedException;
	void markBufferAsReady(double buffer[][]) throws InterruptedException;
	void finalizeBuffers() throws InterruptedException;

	MultichannelSampleSource getSignalSource();
	NewArtifactConstants getArtifactConstants();


}
