package org.signalml.util.matfiles.elements;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * This class represents the Header of the MAT-file.
 * It consists of: 124B - descriptive header,
 * 2B - MAT-file version, 2B - endian indicator.
 *
 * @author Piotr Szachewicz
 */
public class Header implements IMatFileElement {

	/**
	 * The size of the descriptive header.
	 */
	protected static final int DESCRIPTIVE_HEADER_SIZE = 124;

	/**
	 * Returns the descriptive text for this MAT-file.
	 * Descriptive text contains some information about
	 * this MAT-file.
	 * @return the descriptive text.
	 */
	protected String getDescriptiveText() {
		StringBuilder sb = new StringBuilder();
		sb.append("MATLAB 5.0 MAT-file, Platform: ");
		sb.append(System.getProperty("os.name"));
		sb.append(", Created on: ");

		Calendar today = Calendar.getInstance();

		sb.append(today.getTime().toString());

		sb.append(" using Svarog (http://svarog.pl)");
		return sb.toString();
	}

	/**
	 * Returns the value that should be written as a
	 * version subfield of this header.
	 * @return the MAT-file version value.
	 */
	protected short getVersion() {
		return (short) 0x0100;
	}

	/**
	 * Returns the endian indicator field value.
	 * @return the endian indicator field.
	 */
	protected byte[] getEndianIndicator() {
		/**
		 * If these are in reversed order (IM indead of MI)
		 * then the "program reading the MAT-file must perform byte-swapping
		 * to interpret the data in the MAT-file correctly".
		 * (MAT-file specification).
		 */
		return new byte[] {(byte)'M', (byte)'I'};
	}

	@Override
	public void write(DataOutputStream dataOutputStream) throws IOException {
		byte[] text = getDescriptiveText().getBytes();
		dataOutputStream.write(text);
		for (int i = text.length; i < DESCRIPTIVE_HEADER_SIZE; i++)
			dataOutputStream.write((byte) 0);

		dataOutputStream.write((byte)(getVersion() >> 8));
		dataOutputStream.write((byte) getVersion());

		dataOutputStream.write(getEndianIndicator());
	}

	@Override
	public int getTotalSizeInBytes() {
		return DESCRIPTIVE_HEADER_SIZE + 4;
	}

}
