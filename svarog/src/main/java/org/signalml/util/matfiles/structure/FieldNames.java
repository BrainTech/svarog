package org.signalml.util.matfiles.structure;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.signalml.util.matfiles.DataElement;
import org.signalml.util.matfiles.types.DataType;

public class FieldNames extends DataElement {

	private List<String> fieldNames = new ArrayList<String>();

	private int fieldNameMaximumSize;

//	public FieldNames(List<String> fieldNames) {
//		super(DataType.MI_UTF8);
//		this.fieldNames = fieldNames;
//
//		sizeInBytes = fieldNames.size() * FieldNameLength.MAXIMUM_FIELD_NAME_LENGTH;
//	}

	public FieldNames(List<String> fieldNames, int fieldNameMaximumSize) {
		super(DataType.MI_INT8);
		this.fieldNames = fieldNames;

		this.fieldNameMaximumSize = fieldNameMaximumSize;
	}

	@Override
	protected int getSizeInBytes() {
		return fieldNames.size() * fieldNameMaximumSize;
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		super.write(dataOutputStream);

		for (String fieldName: fieldNames) {
			for (char c: fieldName.toCharArray()) {
				dataOutputStream.write(c);
			}

			for (int i = fieldName.length(); i < fieldNameMaximumSize; i++)
				dataOutputStream.write(0);
		}

		writePadding(dataOutputStream);
	}

}
