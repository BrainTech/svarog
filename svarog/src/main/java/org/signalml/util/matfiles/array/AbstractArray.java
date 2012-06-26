package org.signalml.util.matfiles.array;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.array.elements.ArrayFlags;
import org.signalml.util.matfiles.array.elements.ArrayName;
import org.signalml.util.matfiles.array.elements.DimensionsArray;
import org.signalml.util.matfiles.elements.DataElement;
import org.signalml.util.matfiles.types.ArrayClass;
import org.signalml.util.matfiles.types.DataType;

public abstract class AbstractArray extends DataElement {

	protected ArrayClass arrayClass;

	protected ArrayFlags arrayFlags;
	protected DimensionsArray dimensionsArray;
	protected ArrayName arrayName;

	public AbstractArray(ArrayClass arrayClass, String arrayName) {
		super(DataType.MI_MATRIX);

		this.arrayClass = arrayClass;
		this.arrayFlags = new ArrayFlags(arrayClass);
		this.arrayName = new ArrayName(arrayName);
	}

	protected int getSizeInBytesWithoutPadding() {
		int sizeInBytes =
				arrayFlags.getTotalSizeInBytes()
				+ dimensionsArray.getTotalSizeInBytes()
				+ this.arrayName.getTotalSizeInBytes();
		sizeInBytes += 8;
		sizeInBytes += getNumberOfElements() * arrayClass.getArrayElementDataType().getDataTypeSizeInBytes();

		return sizeInBytes;
	}

	@Override
	protected int getSizeInBytes() {
		int sizeInBytes = getSizeInBytesWithoutPadding();
		int paddingSize = getPaddingSizeInBytes();

		return sizeInBytes + paddingSize;
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		super.write(dataOutputStream);

		arrayFlags.write(dataOutputStream);
		dimensionsArray.write(dataOutputStream);
		arrayName.write(dataOutputStream);
	}

	@Override
	protected int getPaddingSizeInBytes() {
		return calculatePaddingSizeInBytes(getSizeInBytesWithoutPadding());
	}

	protected abstract int getNumberOfElements();

	public int getTotalSizeInBytes() {
		return 8 + getSizeInBytes();
	}

}
