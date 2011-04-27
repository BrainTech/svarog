package org.signalml.plugin.newartifact.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;


public class NewArtifactDataReader implements INewArtifactDataReader {
	private final File sourceFile;
	private final int channelCount;

	private static final int BUFFER_SIZE = 8192;

	public NewArtifactDataReader(File sourceFile,
				     int channelCount) {
		this.sourceFile = sourceFile;
		this.channelCount = channelCount;
	}

	public double[][] read() throws IOException {
		byte rawBuffer[] = new byte[NewArtifactDataReader.BUFFER_SIZE];
		ByteBuffer byteBuffer = ByteBuffer.wrap(rawBuffer);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		FloatBuffer floatBuffer;
		int j, readBytes;
		LinkedList<Vector<Double>> dataVectors = new LinkedList<Vector<Double>>();
		for (int i = 0; i < this.channelCount; ++i) {
			dataVectors.add(new Vector<Double>(NewArtifactDataReader.BUFFER_SIZE));
		}

		InputStream stream = new FileInputStream(this.sourceFile);

		try {
			byteBuffer.clear();
			Iterator<Vector<Double>> it = dataVectors.iterator();
			while ((readBytes = stream.read(rawBuffer)) > 0) {
				floatBuffer = byteBuffer.asFloatBuffer();
				j = 0;
				while (j < (readBytes >> 2)) {
					if (!it.hasNext()) {
						it = dataVectors.iterator();
					}
					it.next().add((double) floatBuffer.get(j));
					j++;
				}
				byteBuffer.clear();
			}
		} finally {
			stream.close();
		}

		double result[][] = new double[this.channelCount][];
		Iterator<Vector<Double>> it = dataVectors.iterator();
		j = 0;
		while (it.hasNext()) {
			Vector<Double> v = it.next();
			double channel[] = result[j] = new double[v.size()];
			for (int i = 0; i < channel.length; ++i) {
				channel[i] = v.get(i);
			}
			j++;
		}

		return result;
	}

	@Override
	public long getDataSize() throws IOException {
		return this.sourceFile.length() / (4 * this.channelCount);
	}
}
