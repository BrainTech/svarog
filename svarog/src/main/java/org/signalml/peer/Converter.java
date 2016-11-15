package org.signalml.peer;

import java.nio.charset.Charset;

/**
 * Converts string-to-binary and vice versa, using
 * suitable encoding for communication with OBCI.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class Converter {

	private final static String UTF8 = "UTF-8";

	public final static Charset CHARSET = Charset.isSupported(UTF8) ? Charset.forName(UTF8) : Charset.defaultCharset();

	private Converter() {
		// uninstatiable class
	}

	/**
	 * Encodes this String into a sequence of bytes using the given charset,
	 * storing the result into a new byte array.
	 *
	 * @param string  the string to be decoded into bytes
	 * @return  converted byte array
	 */
	public static byte[] bytesFromString(String string) {
		return string.getBytes(CHARSET);
	}

	/**
	 * Constructs a new String by decoding the specified array of bytes
	 * using the suitable charset.
	 *
	 * @param bytes  the bytes to be decoded into characters
	 * @return  converted string
	 */
	public static String stringFromBytes(byte[] bytes) {
		return new String(bytes, CHARSET);
	}
}
