package org.signalml.util.matfiles.types;

import java.io.DataOutputStream;
import java.io.IOException;
import org.signalml.util.matfiles.elements.IMatFileElement;

/**
 * The type of single data item that can be written
 * to a MAT file.
 *
 * @author Piotr Szachewicz
 */
public enum DataType implements IMatFileElement {

	MI_INT8(1, 1),
	MI_UINT8(2 ,1),
	MI_INT16(3, 2),
	MI_UINT16(4, 2),
	MI_INT32(5, 4),
	MI_UINT32(6, 4),
	MI_SINGLE(7, 4),
	MI_DOUBLE(9, 8),
	MI_UTF8(16, 1),
	MI_INT64(12, 8),
	MI_UINT64(13, 8),
	MI_MATRIX(14),
	MI_COMPRESSED(15);

	/**
	 * The value which is written to the MAT file to
	 * indicate each data type.
	 */
	int value;

	/**
	 * Number of bytes which must be used to hold
	 * a single value of a given data type.
	 * E. g. INT32 needs 4 bytes to be written.
	 */
	int sizeInBytes;

	private DataType(int value) {
		this.value = value;
	}

	private DataType(int value, int sizeInBytes) {
		this.value = value;
		this.sizeInBytes = sizeInBytes;
	}

	/**
	 * Returns the number of bytes which must be used to hold
	 * a single value of a given data type.
	 * E. g. INT32 needs 4 bytes to be written.
	 * @return
	 */
	public int getDataTypeSizeInBytes() {
		return sizeInBytes;
	}

	@Override
	public int getTotalSizeInBytes() {
		return 4;
	}

	/**
	 * Returns the value which is written to the MAT file to
	 * indicate each data type.
	 *
	 * @return the value written to MAT file representing
	 * a data type.
	 */
	public int getValue() {
		return value;
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeInt(value);
	}
}
