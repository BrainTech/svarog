/* Util.java created 2007-09-18
 *
 */

package org.signalml.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.Checksum;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.apache.log4j.Logger;
import org.signalml.app.document.signal.SignalChecksumProgressMonitor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;

/**
 * Util provides various String to Date conversions, signal checksums, file and String MD5 sums.
 * It also allows to get and change file extension, invert map, save system information, split String to separate lines, etc.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * 		parts based on code Copyright (C) 2003 Dobieslaw Ircha <dircha@eranet.pl> Artur Biesiadowski <abies@adres.pl> Piotr J. Durka <Piotr-J.Durka@fuw.edu.pl>
 */
public abstract class Util {

	protected static final Logger logger = Logger.getLogger(Util.class);

	/**
	 * line separator
	 */
	public static final String LINE_SEP = System.getProperty("line.separator");

	/**
	 * file separator
	 */
	public static final String FILE_SEP = System.getProperty("file.separator");

	/**
	 * numbers in base sixteen
	 */
	public static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static final Pattern WINDOWS_OS_PATTERN = Pattern.compile(".*[Ww]indows.*");
	public static final Pattern LINUX_OS_PATTERN = Pattern.compile(".*[Ll]inux.*");
	public static final Pattern MAC_OS_PATTERN = Pattern.compile(".*[Mm]ac.*");

	private static Pattern fqClassNamePattern = null;

	/**
	 * Checks if two objects are equal (if both of them are nulls, it returns true even if they are not of the same class).
	 * @param o1 first object to compare
	 * @param o2 second object to compare
	 * @return true if specified objects are equal
	 */
	public static boolean equalsWithNulls(Object o1, Object o2) {
		if (o1 == null) {
			return (o2 == null) ? true : false;
		}
		return o1.equals(o2);
	}

	/**
	 * Computes specified checksum for given file and monitor length of processed data.
	 * @param file file to read signal from
	 * @param checksumTypes array of checksums to count
	 * @param monitor monitors legth of processed data once every 1000 iterations
	 * @return array of computed checksum for given file
	 * @throws SignalMlException when in array of checksums exists unsupported checksum type or when occur any problem while reading data from file
	 */
	public static SignalChecksum[] getSignalChecksums(File file, String[] checksumTypes, SignalChecksumProgressMonitor monitor) throws SignalMLException {
		return getSignalChecksums(file, checksumTypes, 0, (int) file.length(), monitor);
	}

	/**
	 * Computes specified checksum for given file and monitor length of processed data. It also can skip given number of starting data and process only specified number of data.
	 * @param file file to read signal from
	 * @param checksumTypes array of checksums to count
	 * @param offset number of starting data to skip (if it is negative, no bytes are skipped)
	 * @param length length of data to process
	 * @param monitor monitors legth of processed data once every 1000 iterations
	 * @return array of computed checksum for given file
	 * @throws SignalMlException when in array of checksums exists unsupported checksum type or when occur any problem while reading data from file
	 */
	public static SignalChecksum[] getSignalChecksums(File file, String[] checksumTypes, int offset, int length, SignalChecksumProgressMonitor monitor) throws SignalMLException {

		if (checksumTypes.length == 0) {
			return new SignalChecksum[0];
		}

		Checksum[] checksums = new Checksum[checksumTypes.length];
		int i;

		for (i=0; i<checksumTypes.length; i++) {

			if (checksumTypes[i].equalsIgnoreCase("crc32")) {
				checksums[i] = new java.util.zip.CRC32();
			} else if (checksumTypes[i].equalsIgnoreCase("adler32")) {
				checksums[i] = new java.util.zip.Adler32();
			} else {
				throw new SignalMLException("error.noSuchChecksumType");
			}

		}

		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			byte[] buf = new byte[8*1024];
			is.skip(offset);
			int iteration = 0;

			long cnt, lengthProceeded = 0;
			while (((cnt = is.read(buf)) > 0) && (lengthProceeded < length)) {

				if (monitor != null) {
					if (monitor.isCancelled()) {
						return null;
					}
					iteration = (iteration + 1) % 1000;
					if (iteration == 0) {
						monitor.setBytesProcessed(lengthProceeded);
					}
				}

				if (lengthProceeded + cnt <= length) {
					for (i=0; i<checksumTypes.length; i++) {
						checksums[i].update(buf, 0, (int) cnt);
					}
					lengthProceeded += cnt;
				} else {
					for (i=0; i<checksumTypes.length; i++) {
						checksums[i].update(buf, 0, (int)(length - lengthProceeded));
					}
					lengthProceeded = length;
				}

			}
		} catch (IOException e) {
			throw new SignalMLException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
				}
			}
		}

		SignalChecksum[] results = new SignalChecksum[checksums.length];

		long checksum;
		byte[] bytes;
		for (i=0; i<checksumTypes.length; i++) {

			checksum = checksums[i].getValue();
			bytes = new byte[4];

			bytes[0] = (byte)((checksum & 0xFF000000) >> 24);
			bytes[1] = (byte)((checksum & 0xFF0000) >> 16);
			bytes[2] = (byte)((checksum & 0xFF00) >> 8);
			bytes[3] = (byte)(checksum & 0xFF);

			results[i] = new SignalChecksum(checksumTypes[i], offset, length, toHexString(bytes));

		}

		return results;

	}

	/**
	 * Returs MD5 checksum of specified file.
	 * @param file file to count checksum
	 * @return String with MD5 checksum
	 * @throws IOException if an I/O error occurs
	 */
	public static String getFileSignature(File file) throws IOException {
		long len = file.length();
		FileReader fr = new FileReader(file);
		char[] cbuf = new char[(int) len];
		fr.read(cbuf);
		fr.close();
		return toMD5String(new String(cbuf));
	}

	/**
	 * Returns hexadecimal representation of specified array of bytes.
	 * @param bytes data to convert
	 * @return String with hexadecimal representation of given data
	 */
	public static String toHexString(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			hexString.append(HEX_CHARS[(bytes[i] & 0xF0) >> 4]).append(HEX_CHARS[bytes[i] & 0x0F]);
		}
		return hexString.toString();
	}

	/**
	 * Resturns MD5 checksum of specified String.
	 * @param s String to count checksum
	 * @return String with MD5 checksum
	 * @throws NullPointerException if given String is null
	 */
	public static String toMD5String(CharSequence s) {
		if (s == null)
			throw new NullPointerException();

		byte[] bytes;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(s.toString().getBytes());
			bytes = md5.digest();
		} catch (NoSuchAlgorithmException ex) {
			logger.error("Failed to create a digest", ex);
			return "";
		}

		return toHexString(bytes);
	}

	/**
	 * Returns random String in hexadecimal representation of specified length.
	 * @param byteCount length of target String
	 * @return random String
	 */
	public static String getRandomHexString(int byteCount) {
		byte[] bytes = new byte[byteCount];
		(new Random()).nextBytes(bytes);
		return toHexString(bytes);
	}

	/**
	 * Returns extension of specified file. The extension can be returned with or without preceding dot.
	 * @param file File to extract extension from
	 * @param withDot If is true then returned extension is proceded by a dot.
	 * @return extension of specified file
	 */
	public static String getFileExtension(File file, boolean withDot) {
		if (file == null) {
			return null;
		}
		String path = file.getAbsolutePath();
		int dotAt = path.lastIndexOf('.');
		if (dotAt < 0) {
			return null;
		}
		if (!withDot) {
			dotAt++;
		}
		return path.substring(dotAt);
	}

	/**
	 * Returns name of specified file without its extension.
	 * @param file File to extract name from
	 * @return name of File
	 */
	public static String getFileNameWithoutExtension(File file) {
		if (file == null) {
			return null;
		}

		String path = file.getName();
		int dotAt = path.lastIndexOf('.');
		if (dotAt < 0) {
			return null;
		}

		return path.substring(0,dotAt);
	}

	/**
	 * Adds specified extension to file. If any extension exists before, it is changed.
	 * @param file File to change extension
	 * @param extension new extension
	 * @return file with new extension
	 */
	public static File changeOrAddFileExtension(File file, String extension) {

		String name = file.getName();
		File parent = file.getParentFile();

		int dotAt = name.lastIndexOf('.');
		if (dotAt < 0) {
			name = name + "." + extension;
		} else {
			name = name.substring(0, dotAt+1) + extension;
		}

		return new File(parent, name);

	}

	/**
	 * Inverts Map of Strings (values become keys, keys become values).
	 * @param input Map to invert
	 * @return inverted Map
	 */
	public static HashMap<String,String> invertStringMap(Map<String,String> input) {

		HashMap<String,String> output = new HashMap<>(input.size());

		Set<Map.Entry<String,String>> entries = input.entrySet();
		for (Map.Entry<String, String> entry : entries) {
			output.put(entry.getValue(), entry.getKey());
		}

		return output;

	}

	/**
	 * Finds and replaces every key from specified Map in specified String by value from this map in brackets '${...}'.
	 * Map can be inverted before this operation.
	 * @param input String to process
	 * @param tokenMap map to take keys and values from for replacing process
	 * @param invertMap if is true then Map is inverted before replacing
	 * @return String with every occurence of key from Map in input String replaced by its value
	 */
	public static String substituteForTokens(String input, Map<String,String> tokenMap, boolean invertMap) {

		if (invertMap) {
			tokenMap = invertStringMap(tokenMap);
		}

		input = input.replaceAll("\\$", "\\$\\$");

		Set<String> keySet = tokenMap.keySet();
		boolean somethingDone = false;
		int index;
		do {
			somethingDone = false;
			for (String key : keySet) {
				index = input.indexOf(key);
				if (index >= 0) {
					input = input.substring(0,index) + "${" + tokenMap.get(key) + "}" + input.substring(index+key.length());
					somethingDone = true;
					break;
				}
			}
		} while (somethingDone);

		return input;

	}

	/**
	 * Finds and replaces every key from specified Map in brackets '${...}' in specified String by value from this map in brackets.
	 * @param input String to process
	 * @param tokenMap map to take keys and values from for replacing process
	 * @return String with every occurence of key from Map in input String replaced by its value
	 */
	public static String expandTokens(String input,  Map<String,String> tokenMap) {

		Set<String> keySet = tokenMap.keySet();
		boolean somethingDone = false;
		int index;
		do {
			somethingDone = false;
			for (String key : keySet) {
				String skey = "${" + key + "}";
				index = input.indexOf(skey);
				if (index >= 0) {
					input = input.substring(0,index) + tokenMap.get(key) + input.substring(index+skey.length());
					somethingDone = true;
					break;
				}
			}
		} while (somethingDone);

		return input.replaceAll("\\$\\$", "\\$");

	}

	/**
	 * Checks whether s looks like a valid unicode string and if
	 * any of the characters of s are control characters.
	 * This is a general sanity check, not enough for security
	 * purposes, mainly useful to avoid display problems.
	 * @param s String to validate
	 * @return false if any of code points in this string are of
	 * Unicode class Cc (control characters) or s contains
	 * truncated codepoints or the first code point is combining.
	 */
	public static boolean hasSpecialChars(String s) {
		final int length = s.length();
		for (int offset = 0; offset < length;) {
			final int codepoint = s.codePointAt(offset);
			final int type = Character.getType(codepoint);
			switch (type) {
			case Character.CONTROL:
			case Character.UNASSIGNED:
			case Character.PRIVATE_USE:
				logger.warn(String.format("string '%s' failed validation at offset %d",
										  s, offset));
				return true;
			case Character.SURROGATE:
				logger.warn(String.format("truncated string '%s' failed validation", s));
				return true;
			default:
				if (offset == 0 && isCombining(codepoint))
					return true;
			}
			offset += Character.charCount(codepoint);
		}
		return false;
	}

	/**
	 * @return true if the codepoint represents a combining character
	 * @seealso http://en.wikipedia.org/wiki/Combining_character#Unicode_ranges
	 */
	public static boolean isCombining(int codepoint) {
		return (codepoint >= 0x0300 && codepoint <= 0x036f) ||
			   (codepoint >= 0x1dc0 && codepoint <= 0x1dff) ||
			   (codepoint >= 0x20d0 && codepoint <= 0x20ff) ||
			   (codepoint >= 0xfe20 && codepoint <= 0xfe2f);
	}

	/**
	 * Returns String with those elements from specified String which are valid Unicode characters.
	 * @param s String to trim
	 * @return valid String
	 */
	public static String trimString(String s) {

		StringBuilder sb = new StringBuilder();
		int cnt = s.length();
		int firstGood = -1;
		boolean good = false;
		int code;

		for (int i=0; i<cnt; i++) {

			code = s.codePointAt(i);
			if ((code == 0x9 || code == 0xA || code == 0xD || (code >= 0x20 && code <= 0xD7FF) || (code >= 0xE000 && code <= 0xFFFD))) {
				if (!good) {
					good = true;
					firstGood = i;
				}
			} else {
				if (good) {
					good = false;
					sb.append(s, firstGood, i);
				}
			}

		}

		if (good) {
			sb.append(s, firstGood, cnt);
		}

		return sb.toString();

	}

	/**
	 * Returns array of String which contains every separate line from specified String with limited number of characters in one line
	 * NOTE: Single word will not be splitted even if it is longer then limit.
	 * @param string String to split
	 * @param limit maximal number of characters in one line
	 * @return splitted String
	 */
	public static String[] splitTextIntoLines(String string, int limit) {

		LinkedList<String> list = new LinkedList<>();

		int firstLineChar = 0;
		int lastSpaceChar = -1;
		int len = string.length();
		int i;
		String line;
		char ch;

		for (i=0; i<len; i++) {

			ch = string.charAt(i);

			if (ch == '\n') {
				line = string.substring(firstLineChar, ((i != 0 && string.charAt(i-1) == '\r') ? i-1 : i));
				list.add(line);
				firstLineChar = i+1;
				lastSpaceChar = -1;
				continue;
			}

			if ((i - firstLineChar) > limit) {
				if (lastSpaceChar < 0) {
					if (ch == ' ' || ch == '\t') {
						line = string.substring(firstLineChar, i);
						list.add(line);
						firstLineChar = i+1;
						continue;
					}
				} else {
					line = string.substring(firstLineChar, lastSpaceChar);
					list.add(line);
					firstLineChar = lastSpaceChar+1;
					lastSpaceChar = -1;
					continue;
				}
			}

		}

		if (firstLineChar < len) {
			list.add(string.substring(firstLineChar));
		}

		String[] result = new String[list.size()];
		list.toArray(result);

		return result;

	}

	/**
	 * Writes system information to "logger" variable.
	 */
	public static void dumpDebuggingInfo() {

		Runtime runtime = Runtime.getRuntime();

		logger.info("Java vendor: " + System.getProperty("java.vendor"));
		logger.info("Java version: " + System.getProperty("java.version"));
		logger.info("Java home: " + System.getProperty("java.home"));
		logger.info("Operating system: " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
		logger.info("Memory max: " + runtime.maxMemory() + " current: " + runtime.totalMemory() + " free: " + runtime.freeMemory());
		logger.info("Class path: " + System.getProperty("java.class.path"));

		Properties properties = System.getProperties();
		Set<String> names = properties.stringPropertyNames();
		for (String s : names) {
			logger.debug("Property [" + s + "] -> [" + properties.getProperty(s) + "]");
		}

	}

	/**
	 * Calls Thread.sleep() and returns normally, even if the thread is interrupted.
	 * The exact duration of sleep is subject to the precision and accuracy
	 * of system timers and schedulers. The thread does not lose ownership of any monitors.
	 *
	 * @param millis  the length of time to sleep in milliseconds
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ex) {
			logger.warn("Thread.sleep was interrupted");
		}
	}

	/**
	 * Compresses specified data and encodes it using Base64.
	 * @param data Data to process
	 * @return String representation of compressed and encoded data
	 */
	public static String compressAndBase64Encode(byte[] data) {

		if (data == null) {
			return null;
		}
		if (data.length == 0) {
			return "";
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
		Deflater deflater = new Deflater(9);
		deflater.setInput(data);
		deflater.finish();

		byte[] temp = new byte[8192];
		int cnt;

		do {

			cnt = deflater.deflate(temp);
			if (cnt > 0) {
				baos.write(temp,0,cnt);
			}

		} while (cnt > 0);

		deflater.end();

		byte[] compressed = baos.toByteArray();

		// caution: request processing is considerably slower when Base64 output is wrapped
		// (probably because of multiple text nodes being added to the dom tree)
		return Base64.encodeToString(compressed, false);

	}

	/**
	 * Decompresses specified data and decodes it using Base64.
	 * @param encoded data to process
	 * @return String representation of decompressed and decoded data
	 * @throws DataFormatException when data is not correctly compressed
	 */
	public static byte[] base64DecodeAndDecompress(String encoded) throws DataFormatException {

		if (encoded == null) {
			return null;
		}
		if (encoded.isEmpty()) {
			return new byte[0];
		}

		byte[] compressed = Base64.decodeToBytes(encoded);

		ByteArrayOutputStream baos = new ByteArrayOutputStream(compressed.length);

		Inflater inflater = new Inflater();
		inflater.setInput(compressed);

		byte[] temp = new byte[8192];
		int cnt;

		do {

			cnt = inflater.inflate(temp);
			if (cnt > 0) {
				baos.write(temp,0,cnt);
			}

		} while (cnt > 0);

		if (!inflater.finished()) {
			throw new DataFormatException(_("Bad compressed data"));
		}

		inflater.end();

		return baos.toByteArray();

	}

	/**
	 * Returns MD5 hash of given data.
	 * @param userName name of the user
	 * @param loginTime time of logging in
	 * @param sharedSecret some shared message
	 * @return MD5 hash of given data
	 */
	public static String createSharedSecretToken(String userName, Date loginTime, String sharedSecret) {

		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException ex) {
			throw new SanityCheckException(_("MD5 not supported"), ex);
		}

		String input = userName + "!" + org.signalml.util.FormatUtils.formatTime(loginTime) + "!" + sharedSecret;
		byte[] digestBytes = digest.digest(input.getBytes());

		return toHexString(digestBytes);

	}

	/**
	 * Returns square of specified number.
	 * @param x number to count square
	 * @return square of given number
	 */
	public static double sqr(double x) {
		return (x*x);
	}

	/**
	 * Converts representation of color in RGB model in triplet (r, g, b) to single number in hexadecimal system.
	 * @param red amount of red included (between 0 and 255 inclusive)
	 * @param green amount of green included (between 0 and 255 inclusive)
	 * @param blue amount of blue included (between 0 and 255 inclusive)
	 * @return hexadecimal representation of color in RGB model
	 */
	public static int RGBToInteger(int red,int green,int blue) {
		return 0x00000000|(red<<16)|(green<<8)|blue;
	}

	/**
	 * Check if class name matches pattern ^[a-zA-Z0-9_][a-zA-Z0-9$._]*$
	 * @param fqClassName class name to check
	 * @return true if class name matches pattern above
	 */
	public static boolean validateFqClassName(String fqClassName) {
		if (fqClassNamePattern == null) {
			fqClassNamePattern = Pattern.compile("^[a-zA-Z0-9_][a-zA-Z0-9$._]*$");
		}
		return fqClassNamePattern.matcher(fqClassName).matches();
	}

	/**
	 * Returns actual time and/or date in specified format.
	 * @param dateFormat format of date to count
	 * @return actual time and/or date
	 */
	public static String now(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	/**
	 * Return a new string with the first letter capitalized.
	 * @param string the string to capitalize
	 * @return string with the first letter possibly changed
	 */
	public static String capitalize(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}
}
