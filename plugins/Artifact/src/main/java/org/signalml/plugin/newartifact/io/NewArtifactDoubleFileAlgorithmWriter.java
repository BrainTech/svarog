package org.signalml.plugin.newartifact.io;

import java.io.File;
import java.io.IOException;

import org.signalml.plugin.export.SignalMLException;

public class NewArtifactDoubleFileAlgorithmWriter implements
	INewArtifactAlgorithmWriter {

	private final NewArtifactAlgorithmWriter writer1;
	private final NewArtifactAlgorithmWriter writer2;

	private final double fakeBuffer1[][];
	private final double fakeBuffer2[][];

	public NewArtifactDoubleFileAlgorithmWriter(File targetFile1,
			File targetFile2) throws SignalMLException {
		this.writer1 = new NewArtifactAlgorithmWriter(targetFile1);
		this.writer2 = new NewArtifactAlgorithmWriter(targetFile2);

		this.fakeBuffer1 = new double[1][];
		this.fakeBuffer2 = new double[1][];
	}

	@Override
	public void write(double buffer[][]) throws IOException {
		this.fakeBuffer1[0] = buffer[0];
		this.fakeBuffer2[0] = buffer[1];

		this.writer1.write(this.fakeBuffer1);
		this.writer2.write(this.fakeBuffer2);
	}

	@Override
	public void close() throws IOException {
		this.writer1.close();
		this.writer2.close();
	}

}
