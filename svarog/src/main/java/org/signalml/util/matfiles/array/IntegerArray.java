package org.signalml.util.matfiles.array;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.types.ArrayClass;

/**
 * This class can write an array of integers to a MAT-file.
 *
 * @author Piotr Szachewicz
 */
public class IntegerArray extends GenericArray<Integer> {

	public IntegerArray(String arrayName, Integer[][] values) {
		super(ArrayClass.MX_INT32_CLASS, arrayName, values);
	}

	public IntegerArray(String arrayName, Integer[] values) {
		super(ArrayClass.MX_INT32_CLASS, arrayName);
		Integer[][] val = new Integer[1][values.length];
		for (int i = 0; i < values.length; i++)
			val[0][i] = values[i];

		setValues(val);
	}

	@Override
	protected void writeDataChunk(DataOutputStream dataOutputStream, int i, int j) throws IOException {
		dataOutputStream.writeInt(values[i][j]);
	}

}
