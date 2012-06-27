package org.signalml.util.matfiles.elements;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.types.DataType;

/**
 * This abstract class represents a single element that can be
 * written to a MAT file.
 *
 * @author Piotr Szachewicz
 */
public abstract class DataElement implements IMatFileElement {

	/**
	 * From the MATLAB MAT-file format specification:
	 * "All data that is uncompressed must be aligned on 64-bit boundaries."
	 * This means that each data element must start on a 64-bit (8 bytes) boundary.
	 */
	protected static int MINIMUM_BLOCK_SIZE_IN_BYTES = 8;

	/**
	 * The {@link DataType} of this data element.
	 */
	protected DataType dataType;

	/**
	 * The value of the 'Number of Bytes' field in this data element.
	 * This value does not include the 8-byte tag (tag consists of
	 * a 4-byte integer representing the data type of the element and
	 * a 4-byte integer holding the size of the data type).
	 */
	private int sizeInBytes;

	public DataElement(DataType dataType) {
		this.dataType = dataType;
	}

	public DataElement(DataType dataType, int sizeInBytes) {
		this.dataType = dataType;
		this.sizeInBytes = sizeInBytes;
	}

	/**
	 * Returns the padding size for this element. Padding is used
	 * so that each data element falls on the 64-bit boundaries.
	 * (see: {@link DataElement#MINIMUM_BLOCK_SIZE_IN_BYTES}).
	 *
	 * @return the padding size in bytes.
	 */
	protected int getPaddingSizeInBytes() {
		return calculatePaddingSizeInBytes(getSizeInBytes());
	}

	/**
	 * Calculates the number of bytes that should be written as padding
	 * taking into account the given size.
	 *
	 * @param size the size of the element for which the padding will be
	 * calculated.
	 * @return the padding size.
	 */
	protected int calculatePaddingSizeInBytes(int size) {
		if (size%MINIMUM_BLOCK_SIZE_IN_BYTES == 0)
			return 0;
		else
			return MINIMUM_BLOCK_SIZE_IN_BYTES - size % MINIMUM_BLOCK_SIZE_IN_BYTES;
	}

	/**
	 * Writes the padding bytes to the {@link DataOutputStream}.
	 * @param dataOutputStream
	 * @throws IOException
	 */
	protected void writePadding(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.write(new byte[getPaddingSizeInBytes()]);
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		dataType.write(dataOutputStream);
		dataOutputStream.writeInt(getSizeInBytes());
	}

	/**
	 * Returns the size in bytes field value.
	 * (This size does not include the tag size).
	 * @return the size of this element not including tag.
	 */
	protected int getSizeInBytes() {
		return sizeInBytes;
	}

	@Override
	public int getTotalSizeInBytes() {
		return 8 + getSizeInBytes() + getPaddingSizeInBytes();
	}

}
