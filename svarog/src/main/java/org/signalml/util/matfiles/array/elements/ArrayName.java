package org.signalml.util.matfiles.array.elements;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.elements.DataElement;
import org.signalml.util.matfiles.types.DataType;

public class ArrayName extends DataElement {

	private String name;

	public ArrayName(String name) {
		super(DataType.MI_UTF8);
		this.name = name;
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		super.write(dataOutputStream);
		dataOutputStream.write(name.getBytes());

		writePadding(dataOutputStream);
	}

	@Override
	protected int getSizeInBytes() {
		return name.length() * dataType.getDataTypeSizeInBytes();
	}

}
