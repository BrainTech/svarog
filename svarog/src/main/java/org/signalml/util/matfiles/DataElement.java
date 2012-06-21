package org.signalml.util.matfiles;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.types.DataType;

public abstract class DataElement implements IMatFileElement {

	protected static int MINIMUM_BLOCK_SIZE_IN_BYTES = 8;

	protected DataType dataType;
	private int sizeInBytes;

	public DataElement(DataType dataType) {
		this.dataType = dataType;
	}

	public DataElement(DataType dataType, int sizeInBytes) {
		this.dataType = dataType;
		this.sizeInBytes = sizeInBytes;
	}

	protected int getPaddingSizeInBytes() {
		return calculatePaddingSizeInBytes(getSizeInBytes());
	}

	protected int calculatePaddingSizeInBytes(int size) {
		if (size%MINIMUM_BLOCK_SIZE_IN_BYTES == 0)
			return 0;
		else
			return MINIMUM_BLOCK_SIZE_IN_BYTES - size % MINIMUM_BLOCK_SIZE_IN_BYTES;
	}

	protected void writePadding(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.write(new byte[getPaddingSizeInBytes()]);
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		dataType.write(dataOutputStream);
		dataOutputStream.writeInt(getSizeInBytes());
	}

	protected int getSizeInBytes() {
		return sizeInBytes;
	}

	public int getTotalSizeInBytes() {
		return 8 + getSizeInBytes() + getPaddingSizeInBytes();
	}

}
