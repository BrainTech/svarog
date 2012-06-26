package org.signalml.util.matfiles.array.lazy;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.array.GenericArray;
import org.signalml.util.matfiles.array.elements.DimensionsArray;
import org.signalml.util.matfiles.types.ArrayClass;

public class LazyExportDoubleArray extends GenericArray<Double> {

	private ILazyDoubleArrayDataProvider lazyDataProvider;

	public LazyExportDoubleArray(String arrayName, ILazyDoubleArrayDataProvider lazyDataProvider) {
		super(ArrayClass.MX_DOUBLE_CLASS, arrayName);
		this.lazyDataProvider = lazyDataProvider;
		dimensionsArray = new DimensionsArray(new int[] { lazyDataProvider.getHeight(), lazyDataProvider.getWidth() });
	}

	@Override
	protected int getNumberOfElements() {
		return lazyDataProvider.getHeight() * lazyDataProvider.getWidth();
	}

	@Override
	protected void writeData(DataOutputStream dataOutputStream) throws IOException {
		for (int x = 0; x < lazyDataProvider.getWidth(); x++) {
			double[][] sampleChunk = lazyDataProvider.getDataChunk(x, 1);

			for (int j = 0; j < sampleChunk[0].length; j++)
				for (int i = 0; i < sampleChunk.length; i++)
					dataOutputStream.writeDouble(sampleChunk[i][j]);
		}
	}

	@Override
	protected void writeDataChunk(DataOutputStream dataOutputStream, int i, int j) throws IOException {
		//do nothing
		//it is not needed, nor used in this class
	}

}
