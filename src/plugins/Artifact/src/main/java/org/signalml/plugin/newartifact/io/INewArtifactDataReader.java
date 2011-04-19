package org.signalml.plugin.newartifact.io;

import java.io.IOException;

public interface INewArtifactDataReader {
	public double[][] read() throws IOException;
	public long getDataSize() throws IOException;
}
