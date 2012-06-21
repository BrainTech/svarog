package org.signalml.util.matfiles.array;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.types.ArrayClass;

public class IntegerArray extends GenericArray<Integer>{

	public IntegerArray(String arrayName, Integer[] values) {
		super(ArrayClass.MX_INT32_CLASS, arrayName, values);
	}

	@Override
	protected void writeData(DataOutputStream dataOutputStream) throws IOException {
		for (int value: values) {
			dataOutputStream.writeInt(value);
		}
	}

}
