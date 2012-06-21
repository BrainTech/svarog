package org.signalml.util.matfiles;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class Header implements IMatFileElement {
	/**
	 * Elements:
	 * 124 bytes - descriptive header
	 * 2 bytes - version
	 * 2 bytes - endian indicator (order)
	 */

	protected static final int DESCRIPTIVE_HEADER_SIZE = 124;

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

	protected short getVersion() {
		return (short) 0x0100;
	}

	protected byte[] getEndianIndicator() {
		return new byte[] {(byte)'M', (byte)'I'};
	}

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
		return 128;
	}

}
