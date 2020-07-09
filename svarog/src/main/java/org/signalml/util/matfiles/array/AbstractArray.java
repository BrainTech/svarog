package org.signalml.util.matfiles.array;

import java.io.DataOutputStream;
import java.io.IOException;
import org.signalml.util.matfiles.array.elements.ArrayFlags;
import org.signalml.util.matfiles.array.elements.ArrayName;
import org.signalml.util.matfiles.array.elements.DimensionsArray;
import org.signalml.util.matfiles.elements.DataElement;
import org.signalml.util.matfiles.types.ArrayClass;
import org.signalml.util.matfiles.types.DataType;

/**
 * This class represents an array of elements in a MAT-file.
 *
 * @author Piotr Szachewicz
 */
public abstract class AbstractArray extends DataElement {

	/**
	 * The class of this array.
	 */
	protected ArrayClass arrayClass;

	/**
	 * The array flags subelement of this array.
	 */
	protected ArrayFlags arrayFlags;

	/**
	 * The array containing the dimensions of this array.
	 */
	protected DimensionsArray dimensionsArray;

	/**
	 * The name of this array subelement.
	 */
	protected ArrayName arrayName;

	public AbstractArray(ArrayClass arrayClass, String arrayName) {
		super(DataType.MI_MATRIX);

		this.arrayClass = arrayClass;
		this.arrayFlags = new ArrayFlags(arrayClass);
		this.arrayName = new ArrayName(arrayName);
	}

	/**
	 * Returns the size of this array without counting the padding
	 * in.
	 * @return the size of this array without padding.
	 */
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

	/**
	 * Returns the total number of elements that this array contains.
	 * @return the number of elements in this array
	 */
	protected abstract int getNumberOfElements();

	@Override
	public int getTotalSizeInBytes() {
		return 8 + getSizeInBytes();
	}

}
