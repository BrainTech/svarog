package org.signalml.util.matfiles.array.lazy;

import java.io.DataOutputStream;
import java.io.IOException;
import org.signalml.util.matfiles.array.GenericArray;
import org.signalml.util.matfiles.array.elements.DimensionsArray;
import org.signalml.util.matfiles.types.ArrayClass;

/**
 * This class is able to write a double array to a MAT-file
 * in a lazy mode - this means not the whole array at once,
 * but by asking the {@link ILazyDoubleArrayDataProvider}
 * for consecutive parts of the array.
 *
 * @author Piotr Szachewicz
 */
public class LazyExportDoubleArray extends GenericArray<Double> {

	/**
	 * Provides the data from the double array in a lazy mode.
	 */
	private ILazyDoubleArrayDataProvider lazyDataProvider;

	public LazyExportDoubleArray(String arrayName, ILazyDoubleArrayDataProvider lazyDataProvider) {
		super(ArrayClass.MX_DOUBLE_CLASS, arrayName);
		this.lazyDataProvider = lazyDataProvider;
		dimensionsArray = new DimensionsArray(new int[] { lazyDataProvider.getNumberOfRows(), lazyDataProvider.getNumberOfColumns() });
	}

	@Override
	protected int getNumberOfElements() {
		return lazyDataProvider.getNumberOfRows() * lazyDataProvider.getNumberOfColumns();
	}

	@Override
	protected void writeData(DataOutputStream dataOutputStream) throws IOException {
		int samplesToGet = 1024;
		for (int x = 0; x < lazyDataProvider.getNumberOfColumns(); x += samplesToGet) {

			if (x + samplesToGet > lazyDataProvider.getNumberOfColumns()) {
				samplesToGet = lazyDataProvider.getNumberOfColumns() - x;
			}

			double[][] sampleChunk = lazyDataProvider.getDataChunk(x, samplesToGet);

			for (int j = 0; j < sampleChunk[0].length; j++)
				for (double[] samples : sampleChunk) {
					dataOutputStream.writeDouble(samples[j]);
				}
		}
	}

	@Override
	protected void writeDataChunk(DataOutputStream dataOutputStream, int i, int j) throws IOException {
		//do nothing
		//this method is not used in this class
	}

}
