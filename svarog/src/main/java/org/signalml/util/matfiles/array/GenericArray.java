package org.signalml.util.matfiles.array;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.array.elements.DimensionsArray;
import org.signalml.util.matfiles.types.ArrayClass;
import org.signalml.util.matfiles.types.DataType;

/**
 * This abstract class can write an array containing different types of elements
 * to the MAT-file.
 *
 * @author Piotr Szachewicz
 * @param <T> the type of objects that this array can store.
 */
public abstract class GenericArray<T extends Object> extends AbstractArray {

	protected T[][] values;

	public GenericArray(ArrayClass arrayClass, String arrayName, T[][] values) {
		super(arrayClass, arrayName);
		setValues(values);
	}

	public GenericArray(ArrayClass arrayClass, String arrayName) {
		super(arrayClass, arrayName);
	}

	/**
	 * Sets the values that this array stores.
	 * @param values
	 */
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

		// size and type
		DataType arrayDataType = arrayClass.getArrayElementDataType();
		arrayDataType.write(dataOutputStream);

		dataOutputStream.writeInt(getNumberOfElements() * arrayDataType.getDataTypeSizeInBytes());

		writeData(dataOutputStream);
		writePadding(dataOutputStream);
	}

	/**
	 * Writes the data part of the array to the output stream.
	 * @param dataOutputStream
	 * @throws IOException
	 */
	protected void writeData(DataOutputStream dataOutputStream) throws IOException {
		for (int j = 0; j < values[0].length; j++)
			for (int i = 0; i < values.length; i++)
				writeDataChunk(dataOutputStream, i, j);
	}

	/**
	 * Write a single element of the array to the {@link DataOutputStream}.
	 * @param dataOutputStream
	 * @param i the number of row of the element to be written.
	 * @param j the number of column of the element to be written.
	 * @throws IOException
	 */
	protected abstract void writeDataChunk(DataOutputStream dataOutputStream, int i, int j) throws IOException;

}
