package org.signalml.util.matfiles.array.elements;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.array.AbstractArray;
import org.signalml.util.matfiles.elements.DataElement;
import org.signalml.util.matfiles.types.DataType;

/**
 * This class represents the part of the {@link AbstractArray} that
 * holds the name of the array.
 *
 * @author Piotr Szachewicz
 */
public class ArrayName extends DataElement {

	/**
	 * The name of the array.
	 */
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
