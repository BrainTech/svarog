package org.signalml.util.matfiles.array.elements;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.elements.DataElement;
import org.signalml.util.matfiles.types.DataType;

public class DimensionsArray extends DataElement {

	private int[] dimensions;

	public DimensionsArray(int[] dimensions) {
		super(DataType.MI_UINT32); //documentation - INT32, JMatio UINT32
		this.dimensions = dimensions;
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		super.write(dataOutputStream);

		for (int dimension: dimensions) {
			dataOutputStream.writeInt(dimension);
		}
	}

	@Override
	protected int getSizeInBytes() {
		return dimensions.length * dataType.getDataTypeSizeInBytes();
	}

}
