package org.signalml.util.matfiles.structure;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.signalml.util.matfiles.array.AbstractArray;
import org.signalml.util.matfiles.array.DimensionsArray;
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

	public void addElement(String key, AbstractArray array) {
		keys.add(key);
		arrays.add(array);
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
		fieldNameLength = new FieldNameLength(keys);
		fieldNames = new FieldNames(keys, fieldNameLength.getFieldNameMaximumSize());

		super.write(dataOutputStream);

		arrayFlags.write(dataOutputStream);
		dimensionsArray.write(dataOutputStream);
		arrayName.write(dataOutputStream);

//		//size and type
//		DataType arrayDataType = arrayClass.getArrayElementDataType();
//		arrayDataType.write(dataOutputStream);
//
//		dataOutputStream.writeInt(getNumberOfElements() * arrayDataType.getTypeSizeInBytes());

		//data
		fieldNameLength.write(dataOutputStream);
		fieldNames.write(dataOutputStream);

		for (AbstractArray array: arrays) {
			array.write(dataOutputStream);
		}

		writePadding(dataOutputStream);
	}

}
