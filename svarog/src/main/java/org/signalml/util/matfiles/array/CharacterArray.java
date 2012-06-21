package org.signalml.util.matfiles.array;

import java.io.DataOutputStream;
import java.io.IOException;

import org.signalml.util.matfiles.types.ArrayClass;

public class CharacterArray extends GenericArray<Character>{

	public CharacterArray(String arrayName, String value) {
		super(ArrayClass.MX_CHAR_CLASS, arrayName);

		values = new Character[value.length()];
		for (int i = 0; i < value.length(); i++) {
			values[i] = value.charAt(i);
		}
		setValues(values);
	}

	@Override
	protected void writeData(DataOutputStream dataOutputStream) throws IOException {
		for (Character c: values)
			dataOutputStream.writeByte(c);
	}

}
