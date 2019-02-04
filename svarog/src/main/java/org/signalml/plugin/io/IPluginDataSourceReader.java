package org.signalml.plugin.io;

public interface IPluginDataSourceReader {
	public void getSample(double buffer[][]) throws InterruptedException;
	public boolean hasMoreSamples() throws InterruptedException;
}
