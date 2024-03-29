package org.signalml.util.matfiles.structure.elements;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.signalml.util.matfiles.elements.DataElement;
import org.signalml.util.matfiles.types.DataType;

/**
 * This class is responsible for writing the field names
 * structure subelement to the MAT file. It contains all
 * field names that are used in the structure.
 *
 * @author Piotr Szachewicz
 */
public class FieldNames extends DataElement {

	private List<String> fieldNames = new ArrayList<>();
	private int fieldNameMaximumSize;

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
