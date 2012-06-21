package org.signalml.util.matfiles.array;

import org.signalml.util.matfiles.DataElement;
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
	protected int getPaddingSizeInBytes() {
		return calculatePaddingSizeInBytes(getSizeInBytesWithoutPadding());
	}

	protected abstract int getNumberOfElements();

	public int getTotalSizeInBytes() {
		return 8 + getSizeInBytes();
	}

}
