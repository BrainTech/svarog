package org.signalml.util.matfiles.array;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.domain.signal.eeglab.LazySampleProvider;
import org.signalml.util.matfiles.types.ArrayClass;

public class LazyExportDoubleArray extends GenericArray<Double> {

	private LazySampleProvider sampleProvider;

	public LazyExportDoubleArray(String arrayName, LazySampleProvider sampleProvider) {
		super(ArrayClass.MX_DOUBLE_CLASS, arrayName);
		this.sampleProvider = sampleProvider;
		dimensionsArray = new DimensionsArray(new int[] { sampleProvider.getHeight(), sampleProvider.getWidth() });
	}

	@Override
	protected int getNumberOfElements() {
		return sampleProvider.getHeight() * sampleProvider.getWidth();
	}

	@Override
	protected void writeData(DataOutputStream dataOutputStream) throws IOException {
		for (int x = 0; x < sampleProvider.getWidth(); x++) {
			double[][] sampleChunk = sampleProvider.getSampleChunk(x, 1);

			for (int j = 0; j < sampleChunk[0].length; j++)
				for (int i = 0; i < sampleChunk.length; i++)
					dataOutputStream.writeDouble(sampleChunk[i][j]);
		}
	}

	@Override
	protected void writeDataChunk(DataOutputStream dataOutputStream, int i, int j) throws IOException {
	}

}
