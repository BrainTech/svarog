package org.signalml.util.matfiles.array;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.DataElement;
import org.signalml.util.matfiles.types.ArrayClass;
import org.signalml.util.matfiles.types.DataType;

public class ArrayFlags extends DataElement {

	private boolean complex = false;
	private boolean global = false;
	private boolean logical = false;

	private ArrayClass arrayClass;

	public ArrayFlags(ArrayClass arrayClass) {
		super(DataType.MI_UINT32);
		this.arrayClass = arrayClass;
	}

	public ArrayFlags(ArrayClass arrayClass, boolean complex, boolean global, boolean logical) {
		super(DataType.MI_UINT32);
		this.arrayClass = arrayClass;

		this.complex = complex;
		this.global = global;
		this.logical = logical;
	}

	@Override
	protected int getSizeInBytes() {
		return 2 * dataType.getDataTypeSizeInBytes();
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		super.write(dataOutputStream);

		dataOutputStream.write(new byte[2]);

		byte flags = 0;
		if (complex)
			flags |= 8;
		if (global)
			flags |= 4;
		if (logical)
			flags |= 2;

		dataOutputStream.write(flags);
		dataOutputStream.write(arrayClass.getValue());

		dataOutputStream.write(new byte[4]);
	}

}
