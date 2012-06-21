package org.signalml.util.matfiles.array;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.types.ArrayClass;
import org.signalml.util.matfiles.types.DataType;

public abstract class GenericArray<T extends Object> extends AbstractArray {

	protected T[] values;

	public GenericArray(ArrayClass arrayClass, String arrayName, T[] values) {
		super(arrayClass, arrayName);

		dimensionsArray = new DimensionsArray(new int[] {values.length, 1});
		this.values = values;
	}

	public GenericArray(ArrayClass arrayClass, String arrayName) {
		super(arrayClass, arrayName);
	}

	public void setValues(T[] values) {
		dimensionsArray = new DimensionsArray(new int[] {1, values.length});
		this.values = values;
	}

	@Override
	protected int getNumberOfElements() {
		return values.length;
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		super.write(dataOutputStream);

		arrayFlags.write(dataOutputStream);
		dimensionsArray.write(dataOutputStream);
		arrayName.write(dataOutputStream);

		//size and type
		DataType arrayDataType = arrayClass.getArrayElementDataType();
		arrayDataType.write(dataOutputStream);

		dataOutputStream.writeInt(getNumberOfElements() * arrayDataType.getDataTypeSizeInBytes());

		//data
		writeData(dataOutputStream);

		writePadding(dataOutputStream);
	}

	protected abstract void writeData(DataOutputStream dataOutputStream) throws IOException;

}
