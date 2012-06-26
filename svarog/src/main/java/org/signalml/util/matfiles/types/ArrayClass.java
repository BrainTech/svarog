package org.signalml.util.matfiles.types;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.elements.IMatFileElement;

public enum ArrayClass implements IMatFileElement {

	MX_CELL_CLASS(1),
	MX_STRUCT_CLASS(2),
	MX_OBJECT_CLASS(3),
	MX_CHAR_CLASS(4, DataType.MI_UTF8),
	MX_SPARSE_CLASS(5),
	MX_DOUBLE_CLASS(6, DataType.MI_DOUBLE),
	MX_SINGLE_CLASS(7, DataType.MI_SINGLE),
	MX_INT8_CLASS(8, DataType.MI_INT8),
	MX_UINT8_CLASS(9, DataType.MI_UINT8),
	MX_INT16_CLASS(10, DataType.MI_INT16),
	MX_UINT16_CLASS(11, DataType.MI_UINT16),
	MX_INT32_CLASS(12, DataType.MI_INT32),
	MX_UINT32_CLASS(13, DataType.MI_UINT32);

	private int value;
	private DataType arrayElementDataType;

	private ArrayClass(int value) {
		this.value = value;
	}

	public DataType getArrayElementDataType() {
		return arrayElementDataType;
	}

	private ArrayClass(int value, DataType dataType) {
		this(value);
		this.arrayElementDataType = dataType;
	}

	public int getValue() {
		return value;
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeInt(value);
	}

	@Override
	public int getTotalSizeInBytes() {
		return 1;
	}

}
