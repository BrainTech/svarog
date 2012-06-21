package org.signalml.util.matfiles.types;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.IMatFileElement;

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
	MI_MATRIX(14); //number of bytes includes padding

	int value;
	int sizeInBytes;

	private DataType(int value) {
		this.value = value;
	}

	private DataType(int value, int sizeInBytes) {
		this.value = value;
		this.sizeInBytes = sizeInBytes;
	}

	public int getDataTypeSizeInBytes() {
		return sizeInBytes;
	}

	@Override
	public int getTotalSizeInBytes() {
		return 4;
	}

	public int getValue() {
		return value;
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeInt(value);
	}
}
