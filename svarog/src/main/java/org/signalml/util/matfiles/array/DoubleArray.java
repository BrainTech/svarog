package org.signalml.util.matfiles.array;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.types.ArrayClass;

public class DoubleArray extends GenericArray<Double> {

	public DoubleArray(String arrayName, Double[] values) {
		super(ArrayClass.MX_DOUBLE_CLASS, arrayName, values);
	}

	@Override
	protected void writeData(DataOutputStream dataOutputStream) throws IOException {
		for (Double d: values)
			dataOutputStream.writeDouble(d);
	}

}
