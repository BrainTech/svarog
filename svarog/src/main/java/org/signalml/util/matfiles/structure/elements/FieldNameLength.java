package org.signalml.util.matfiles.structure.elements;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import org.signalml.util.matfiles.elements.DataElement;
import org.signalml.util.matfiles.types.DataType;

/**
 * This class is responsible for writing the field name length
 * structure subelement to the MAT file. This element specifies
 * the maximum length of the field name element.
 *
 * @author Piotr Szachewicz
 */
public class FieldNameLength extends DataElement {

	/**
	 * The maximum size of the field name element.
	 */
	private int maximumSize;

	public FieldNameLength(List<String> keys) {
		super(DataType.MI_INT32);

		maximumSize = 0;
		for (String key: keys)
			if (key.length() > maximumSize)
				maximumSize = key.length();

		maximumSize++; //NULL termination
	}

	/**
	 * Returns the maximum length of the field name.
	 * @return
	 */
	public int getFieldNameMaximumSize() {
		return maximumSize;
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeShort(4);
		dataOutputStream.writeShort(dataType.getValue());

		dataOutputStream.writeInt(maximumSize);
	}

}
