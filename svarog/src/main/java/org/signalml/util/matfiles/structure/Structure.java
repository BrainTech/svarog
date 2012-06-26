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

public class Structure extends AbstractArray {

	private List<String> keys = new ArrayList<String>();
	private List<AbstractArray> arrays = new ArrayList<AbstractArray>();

	private FieldNameLength fieldNameLength;
	private FieldNames fieldNames;

	public Structure(String structureName) {
		super(ArrayClass.MX_STRUCT_CLASS, structureName);
		dimensionsArray = new DimensionsArray(new int[] {1, 1});
	}

	/**
	 * Makes this structure to be a structure array.
	 * See: {@link http://www.mathworks.com/help/techdoc/ref/struct.html}
	 * @param keys
	 * @param arrays
	 */
	public void setFieldsForStructureArray(List<String> keys, List<AbstractArray> arrays) {
		this.keys = keys;
		this.arrays = arrays;
		dimensionsArray = new DimensionsArray(new int[] {1, arrays.size() / keys.size()});

		refreshFieldNames();
	}

	public void setField(String key, AbstractArray array) {
		keys.add(key);
		arrays.add(array);

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

		for (AbstractArray array: arrays) {
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

		for (AbstractArray array: arrays) {
			array.write(dataOutputStream);
		}

		writePadding(dataOutputStream);
	}

}
