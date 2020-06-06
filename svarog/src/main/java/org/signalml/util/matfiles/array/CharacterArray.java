package org.signalml.util.matfiles.array;

import java.io.DataOutputStream;
import java.io.IOException;
import org.signalml.util.matfiles.types.ArrayClass;

/**
 * This class can store an array of characters in a MAT-file.
 * An array of characters is represented in Matlab as a string.
 *
 * @author Piotr Szachewicz
 */
public class CharacterArray extends GenericArray<Character>{

	public CharacterArray(String arrayName, String value) {
		super(ArrayClass.MX_CHAR_CLASS, arrayName);

		values = new Character[1][value.length()];
		for (int i = 0; i < value.length(); i++) {
			values[0][i] = value.charAt(i);
		}
		setValues(values);
	}

	@Override
	protected void writeDataChunk(DataOutputStream dataOutputStream, int i, int j) throws IOException {
		dataOutputStream.writeByte(values[i][j]);
	}

}
