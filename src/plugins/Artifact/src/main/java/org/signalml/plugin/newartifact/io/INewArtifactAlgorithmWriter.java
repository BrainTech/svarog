package org.signalml.plugin.newartifact.io;

import java.io.IOException;

public interface INewArtifactAlgorithmWriter {
	public void write(double buffer[][]) throws IOException;
	public void close() throws IOException;
}
