package org.signalml.util.matfiles.structure;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.signalml.util.matfiles.array.AbstractArray;
import org.signalml.util.matfiles.array.elements.DimensionsArray;
import org.signalml.util.matfiles.structure.elements.FieldNameLength;
import org.signalml.util.matfiles.structure.elements.FieldNames;
import org.signalml.util.matfiles.types.ArrayClass;

/**
 * This class holds the data of a Matlab structure.
 * A Matlab structure consists of a list of named
 * fields each holding a value.
 *
 * @author Piotr Szachewicz
 */
public class Structure extends AbstractArray {

	/**
	 * This list of field names for each field.
	 */
	private List<String> keys = new ArrayList<String>();

	/**
	 * The list of fields. Their names are stored in the
	 */
	private List<AbstractArray> fields = new ArrayList<AbstractArray>();

	/**
	 * This field represents the maximum size of a field name.
	 */
	private FieldNameLength fieldNameLength;

	/**
	 * This field is the element of the MAT file holding the
	 * list of field names.
	 */
	private FieldNames fieldNames;

	public Structure(String structureName) {
		super(ArrayClass.MX_STRUCT_CLASS, structureName);
		dimensionsArray = new DimensionsArray(new int[] {1, 1});
	}

	/**
	 * Makes this structure to be a structure array.
	 * See: {@link http://www.mathworks.com/help/techdoc/ref/struct.html}
	 * @param keys list of field names
	 * @param arrays list of field values.
	 */
	public void setFieldsForStructureArray(List<String> keys, List<AbstractArray> arrays) {
		this.keys = keys;
		this.fields = arrays;
		dimensionsArray = new DimensionsArray(new int[] {1, arrays.size() / keys.size()});

		refreshFieldNames();
	}

	/**
	 * Sets the field value in the structure.
	 * @param key field name
	 * @param array field value
	 */
	public void setField(String key, AbstractArray array) {
		keys.add(key);
		fields.add(array);

		refreshFieldNames();
	}

	protected void refreshFieldNames() {
		fieldNameLength = new FieldNameLength(keys);
		fieldNames = new FieldNames(keys, fieldNameLength.getFieldNameMaximumSize());
	}

	@Override
	protected int getNumberOfElements() {
		return keys.size();
	}

	@Override
	protected int getSizeInBytesWithoutPadding() {
		int sizeInBytes =
				arrayFlags.getTotalSizeInBytes()
				+ dimensionsArray.getTotalSizeInBytes()
				+ this.arrayName.getTotalSizeInBytes();
		sizeInBytes += fieldNameLength.getTotalSizeInBytes();
		sizeInBytes += fieldNames.getTotalSizeInBytes();

		for (AbstractArray array: fields) {
			sizeInBytes += array.getTotalSizeInBytes();
		}

		return sizeInBytes;
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		super.write(dataOutputStream);

		//data
		fieldNameLength.write(dataOutputStream);
		fieldNames.write(dataOutputStream);

		for (AbstractArray array: fields) {
			array.write(dataOutputStream);
		}

		writePadding(dataOutputStream);
	}

}
