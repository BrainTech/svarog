package org.signalml.util.matfiles.array;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.array.lazy.LazyExportDoubleArray;
import org.signalml.util.matfiles.types.ArrayClass;

/**
 * This class can store an array of doubles in a MAT-file.
 * To write a double array in a lazy mode, use {@link LazyExportDoubleArray}.
 *
 * @author Piotr Szachewicz
 */
public class DoubleArray extends GenericArray<Double> {

	public DoubleArray(String arrayName, Double[][] values) {
		super(ArrayClass.MX_DOUBLE_CLASS, arrayName, values);
	}

	public DoubleArray(String arrayName, double[] values) {
		super(ArrayClass.MX_DOUBLE_CLASS, arrayName);

		Double[][] val = new Double[1][values.length];
		for (int i = 0; i < values.length; i++)
			val[0][i] = values[i];

		setValues(val);
	}

	@Override
	protected void writeDataChunk(DataOutputStream dataOutputStream, int i, int j) throws IOException {
		dataOutputStream.writeDouble(values[i][j]);
	}

}
