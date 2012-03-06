package org.signalml.plugin.newstager.io;

public interface INewStagerStatsSynchronizer {

	double[][] getWritableBuffer() throws InterruptedException;
	void markBufferAsReady(double[][] buffer) throws InterruptedException;
	double[][] getReadyBuffer() throws InterruptedException;
	void markBufferAsProcessed(double[][] buffer) throws InterruptedException;
	void finalizeBuffers() throws InterruptedException;

}
