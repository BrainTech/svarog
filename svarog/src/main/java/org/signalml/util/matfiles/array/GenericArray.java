package org.signalml.util.matfiles.array;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.types.ArrayClass;
import org.signalml.util.matfiles.types.DataType;

public abstract class GenericArray<T extends Object> extends AbstractArray {

	protected T[][] values;

	public GenericArray(ArrayClass arrayClass, String arrayName, T[][] values) {
		super(arrayClass, arrayName);
		setValues(values);
	}

	public GenericArray(ArrayClass arrayClass, String arrayName) {
		super(arrayClass, arrayName);
	}

	protected void setValues(T[][] values) {
		dimensionsArray = new DimensionsArray(new int[] { values.length, values[0].length });
		this.values = values;
	}

	@Override
	protected int getNumberOfElements() {
		return values.length * values[0].length;
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		super.write(dataOutputStream);

		arrayFlags.write(dataOutputStream);
		dimensionsArray.write(dataOutputStream);
		arrayName.write(dataOutputStream);

		// size and type
		DataType arrayDataType = arrayClass.getArrayElementDataType();
		arrayDataType.write(dataOutputStream);

		dataOutputStream.writeInt(getNumberOfElements() * arrayDataType.getDataTypeSizeInBytes());

		writeData(dataOutputStream);
		writePadding(dataOutputStream);
	}

	protected void writeData(DataOutputStream dataOutputStream) throws IOException {
		for (int j = 0; j < values[0].length; j++)
			for (int i = 0; i < values.length; i++)
				writeDataChunk(dataOutputStream, i, j);
	}

	protected abstract void writeDataChunk(DataOutputStream dataOutputStream, int i, int j) throws IOException;

}
