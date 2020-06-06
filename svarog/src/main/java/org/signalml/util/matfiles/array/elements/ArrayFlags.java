package org.signalml.util.matfiles.array.elements;

import java.io.DataOutputStream;
import java.io.IOException;
import org.signalml.util.matfiles.array.AbstractArray;
import org.signalml.util.matfiles.elements.DataElement;
import org.signalml.util.matfiles.types.ArrayClass;
import org.signalml.util.matfiles.types.DataType;

/**
 * This class represents the array flags subelement of the
 * {@link AbstractArray}. It contains the class of the array
 * and some boolean indicators (complex/global/logical).
 *
 * @author Piotr Szachewicz
 */
public class ArrayFlags extends DataElement {

	/**
	 * Determines if the array for which these array flags
	 * are defined holds complex values or real values.
	 * Warning: complex arrays are not implemented!
	 */
	private boolean complex = false;

	/**
	 * Determines if this array should be loaded as a global
	 * variable in the workspace.
	 */
	private boolean global = false;

	/**
	 * Determines if this array is used for logical indexing.
	 */
	private boolean logical = false;

	/**
	 * The class of this array.
	 */
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
