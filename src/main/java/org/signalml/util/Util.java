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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Checksum;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.log4j.Logger;
import org.signalml.app.document.SignalChecksumProgressMonitor;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.exception.SanityCheckException;
import org.signalml.exception.SignalMLException;

/** Util
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * 		parts based on code Copyright (C) 2003 Dobieslaw Ircha <dircha@eranet.pl> Artur Biesiadowski <abies@adres.pl> Piotr J. Durka	 <Piotr-J.Durka@fuw.edu.pl>
 */
public abstract class Util {

	protected static final Logger logger = Logger.getLogger(Util.class);
	
	public static final String LINE_SEP = System.getProperty( "line.separator" );
	public static final String FILE_SEP = System.getProperty( "file.separator" );
	
	public static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static final Pattern WINDOWS_OS_PATTERN = Pattern.compile(".*[Ww]indows.*");
	public static final Pattern LINUX_OS_PATTERN = Pattern.compile(".*[Ll]inux.*");
	public static final Pattern MAC_OS_PATTERN = Pattern.compile(".*[Mm]ac.*");
	
	private static DecimalFormat twoPlaceFormat = new DecimalFormat("00");

	private static Pattern datePattern = null;
	private static Pattern fqClassNamePattern = null;
	
	public static boolean equalsWithNulls( Object o1, Object o2 ) {
		if( o1 == null ) {
			return ( o2 == null ) ? true : false;
		}
		return o1.equals(o2);
	}
	
	public static SignalChecksum[] getSignalChecksums(File file, String[] checksumTypes, SignalChecksumProgressMonitor monitor) throws SignalMLException { 
		return getSignalChecksums(file, checksumTypes, 0, (int) file.length(), monitor);
	}
		
	public static SignalChecksum[] getSignalChecksums(File file, String[] checksumTypes, int offset, int length, SignalChecksumProgressMonitor monitor) throws SignalMLException {

		if( checksumTypes.length == 0 ) {
			return new SignalChecksum[0];
		}
		
		Checksum[] checksums = new Checksum[checksumTypes.length];
		int i;
		
		for( i=0; i<checksumTypes.length; i++ ) {
		
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
			is = new BufferedInputStream( new FileInputStream(file) );
			byte[] buf = new byte[8*1024];
			is.skip(offset);
			int iteration = 0;

			long cnt, lengthProceeded = 0;
			while (((cnt = is.read(buf)) > 0) && (lengthProceeded < length)) {
				
				if( monitor != null ) {
					if( monitor.isCancelled() ) {
						return null;
					}
					iteration = (iteration + 1) % 1000;
					if( iteration == 0 ) {
						monitor.setBytesProcessed(lengthProceeded);											
					}					
				}

				if (lengthProceeded + cnt <= length) {
					for( i=0; i<checksumTypes.length; i++ ) {
						checksums[i].update(buf, 0, (int) cnt);
					}
					lengthProceeded += cnt;
				} else {
					for( i=0; i<checksumTypes.length; i++ ) {
						checksums[i].update(buf, 0, (int) (length - lengthProceeded));
					}
					lengthProceeded = length;
				}
								
			}
		} catch (IOException e) {
			throw new SignalMLException(e);
		} finally {
			if( is != null ) {
				try {
					is.close();
				} catch (IOException ex) {
				}
			}
		}

		SignalChecksum[] results = new SignalChecksum[checksums.length];
		
		long checksum;
		byte[] bytes;
		for( i=0; i<checksumTypes.length; i++ ) {
						
			checksum = checksums[i].getValue();
			bytes = new byte[4];
	
			bytes[0] = (byte) ((checksum & 0xFF000000) >> 24);
			bytes[1] = (byte) ((checksum & 0xFF0000) >> 16);
			bytes[2] = (byte) ((checksum & 0xFF00) >> 8);
			bytes[3] = (byte) (checksum & 0xFF);

			results[i] = new SignalChecksum(checksumTypes[i], offset, length, toHexString(bytes));
		
		}
			
		return results;
		
	}
	
	public static String getFileSignature(File file) throws IOException {
		long len = file.length();
		FileReader fr = new FileReader(file);
		char[] cbuf = new char[(int) len];
		fr.read(cbuf);
		fr.close();
		return toMD5String(new String(cbuf));
	}
	
	public static String toHexString(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			hexString.append(HEX_CHARS[(bytes[i] & 0xF0) >> 4]).append(HEX_CHARS[bytes[i] & 0x0F]);
		}
		return hexString.toString();
	}
	
	public static String toMD5String(String s) {
		if (s == null) {
			throw new NullPointerException();
		}

		byte[] bytes;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(s.getBytes());
			bytes = md5.digest();
		} catch(NoSuchAlgorithmException ex) {
			logger.error("Failed to create a digest", ex);
			return "";
		}

		return toHexString(bytes);
	}
	
	public static String getRandomHexString(int byteCount) {
		byte[] bytes = new byte[byteCount];
		(new Random()).nextBytes(bytes);		
		return toHexString(bytes);		
	}
	
	public static String getFileExtension(File file, boolean withDot) {
		if( file == null ) {
			return null;
		}
		String path = file.getAbsolutePath();
		int dotAt = path.lastIndexOf('.');
		if( dotAt < 0 ) {
			return null;
		}
		if( !withDot ) {
			dotAt++;
		}
		return path.substring(dotAt);
	}

	public static String getFileNameWithoutExtension(File file) {
		if( file == null ) {
			return null;
		}
		
		String path = file.getName();
		int dotAt = path.lastIndexOf('.');
		if( dotAt < 0 ) {
			return null;
		}

		return path.substring(0,dotAt);
	}
	
	
	public static File changeOrAddFileExtension(File file, String extension) {

		String name = file.getName();
		File parent = file.getParentFile();
		
		int dotAt = name.lastIndexOf('.');
		if( dotAt < 0 ) {
			name = name + "." + extension;
		} else {
			name = name.substring(0, dotAt+1) + extension;
		}
		
		return new File( parent, name );
		
	}
		
	public static HashMap<String,String> invertStringMap(Map<String,String> input) {
		
		HashMap<String,String> output = new HashMap<String, String>(input.size());
		
		Set<Map.Entry<String,String>> entries = input.entrySet();
		for( Map.Entry<String, String> entry : entries ) {
			output.put(entry.getValue(), entry.getKey());
		}
		
		return output;
		
	}
	
	public static String substituteForTokens(String input, Map<String,String> tokenMap, boolean invertMap) {
		
		if( invertMap ) {
			tokenMap = invertStringMap(tokenMap);
		}
		
		input = input.replaceAll("\\$", "\\$\\$");
		
		Set<String> keySet = tokenMap.keySet();
		boolean somethingDone = false;
		int index;
		do {
			somethingDone = false;
			for( String key : keySet ) {
				index = input.indexOf(key);
				if( index >= 0 ) {
					input = input.substring(0,index) + "${" + tokenMap.get(key) + "}" + input.substring(index+key.length());
					somethingDone = true;
					break;
				}			
			}			
		} while( somethingDone );
		
		return input;
	
	}
	
	public static String expandTokens(String input,  Map<String,String> tokenMap) {
		
		Set<String> keySet = tokenMap.keySet();
		boolean somethingDone = false;
		int index;
		do {
			somethingDone = false;
			for( String key : keySet ) {
				String skey = "${" + key + "}";
				index = input.indexOf(skey);
				if( index >= 0 ) {
					input = input.substring(0,index) + tokenMap.get(key) + input.substring(index+skey.length());
					somethingDone = true;
					break;
				}			
			}			
		} while( somethingDone );		
		
		return input.replaceAll("\\$\\$", "\\$");
		
	}
		
	public static void addTime(float time, StringBuilder sb) {
		int intTime = (int) Math.floor( time );
		int remainder = (int) Math.round( (time - intTime)*100 );
		sb.append(twoPlaceFormat.format(intTime/3600)).append(':');
		sb.append(twoPlaceFormat.format((intTime % 3600) / 60)).append(':');
		sb.append(twoPlaceFormat.format(intTime % 60));
		if( remainder > 0 ) {
			sb.append('.').append(twoPlaceFormat.format(remainder));
		}		
	}
	
	public static boolean validateString( String s ) {
		
		int cnt = s.length();
		int code;
		for( int i=0; i<cnt; i++ ) {
			code = s.codePointAt(i);
			// note the negation
			if( ! ( code == 0x9 || code == 0xA || code == 0xD || (code >= 0x20 && code <= 0xD7FF) || (code >= 0xE000 && code <= 0xFFFD) ) ) {
				return false;
			}
		}
		
		return true;
		
	}

	public static String trimString( String s ) {
		
		StringBuilder sb = new StringBuilder();
		int cnt = s.length();
		int firstGood = -1;
		boolean good = false;
		int code;
		
		for( int i=0; i<cnt; i++ ) {
			
			code = s.codePointAt(i);
			if( ( code == 0x9 || code == 0xA || code == 0xD || (code >= 0x20 && code <= 0xD7FF) || (code >= 0xE000 && code <= 0xFFFD) ) ) {
				if( !good ) {
					good = true;
					firstGood = i;
				}					
			} else {
				if( good ) {
					good = false;
					sb.append( s, firstGood, i );
				}
			}
						
		}
		
		if( good ) {
			sb.append( s, firstGood, cnt );
		}
				
		return sb.toString();
		
	}

	public static String[] splitTextIntoLines(String string, int limit) {

		LinkedList<String> list = new LinkedList<String>();
		
		int firstLineChar = 0;
		int lastSpaceChar = -1;
		int len = string.length();
		int i;
		String line;
		char ch;
		
		for( i=0; i<len; i++ ) {
			
			ch = string.charAt(i);
			
			if( ch == '\n' ) {
				line = string.substring(firstLineChar, ( (i != 0 && string.charAt(i-1) == '\r') ? i-1 : i ) );
				list.add(line);
				firstLineChar = i+1;
				lastSpaceChar = -1;
				continue;
			}
			
			if( (i - firstLineChar) > limit ) {
				if( lastSpaceChar < 0 ) {
					if( ch == ' ' || ch == '\t' ) {
						line = string.substring(firstLineChar, i);
						list.add(line);
						firstLineChar = i+1;
						continue;
					}
				} else {
					line = string.substring(firstLineChar, lastSpaceChar);
					list.add( line );
					firstLineChar = lastSpaceChar+1;
					lastSpaceChar = -1;
					continue;
				}				
			}
			
		}
		
		if( firstLineChar < len ) {
			list.add( string.substring(firstLineChar) );
		}
		
		String[] result = new String[list.size()];
		list.toArray(result);
		
		return result;
		
	}
	
	public static void dumpDebuggingInfo() {

		Runtime runtime = Runtime.getRuntime();
		
		logger.info( "Java vendor: " + System.getProperty("java.vendor") );
		logger.info( "Java version: " + System.getProperty("java.version") );
		logger.info( "Java home: " + System.getProperty("java.home") );
		logger.info( "Operating system: " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version") );
		logger.info( "Memory max: " + runtime.maxMemory() + " current: " + runtime.totalMemory() + " free: " + runtime.freeMemory() );
		logger.info( "Class path: " + System.getProperty("java.class.path") );
		
		Properties properties = System.getProperties();
		Set<String> names = properties.stringPropertyNames();
		for( String s : names ) {
			logger.debug( "Property [" + s + "] -> [" + properties.getProperty(s) + "]" );			
		}
						
	}
	
	public static String compressAndBase64Encode( byte[] data ) {
		
		if( data == null ) {
			return null;
		}
		if( data.length == 0 ) {
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
			if( cnt > 0 ) {
				baos.write(temp,0,cnt);
			}
						
		} while( cnt > 0 );
		
		deflater.end();
		
		byte[] compressed = baos.toByteArray();
		
		// caution: request processing is considerably slower when Base64 output is wrapped
		// (probably because of multiple text nodes being added to the dom tree)
		return Base64.encodeToString(compressed, false);
				
	}
	
	public static byte[] base64DecodeAndDecompress( String encoded ) throws DataFormatException {
	
		if( encoded == null ) {
			return null;
		}
		if( encoded.isEmpty() ) {
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
			if( cnt > 0 ) {
				baos.write(temp,0,cnt);
			}
						
		} while( cnt > 0 );
		
		if( !inflater.finished() ) {
			throw new DataFormatException("Bad compressed data");
		}
		
		inflater.end();
		
		return baos.toByteArray();		
		
	}

	public static String formatTime( Date time ) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		
		Formatter formatter = new Formatter();
		formatter.format( "%04d-%02d-%02d %02d:%02d:%02d",
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH)+1,
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND)
		);
		
		return formatter.toString();
		
	}
	
	public static Date parseTime( String time ) throws ParseException {
		
		if( datePattern == null ) {
			datePattern = Pattern.compile( "([0-9]{4})-([0-9]{2})-([0-9]{2}).([0-9]{2}):([0-9]{2}):([0-9]{2})" );
		}
		Matcher matcher = datePattern.matcher(time);
		if( !matcher.matches() ) {
			throw new ParseException( "Bad date format [" + time + "]", 0 );
		}

		try {
			
			int year = Integer.parseInt( matcher.group(1) );
			int month = Integer.parseInt( matcher.group(2) );
			int day = Integer.parseInt( matcher.group(3) );
			int hour = Integer.parseInt( matcher.group(4) );
			int minute = Integer.parseInt( matcher.group(5) );
			int second = Integer.parseInt( matcher.group(6) );

			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.set(year, month-1, day, hour, minute, second);		

			return cal.getTime();
			
		} catch( NumberFormatException ex ) {
			throw new ParseException( "Bad date format [" + time + "]", 0 );
		}
				
	}
	
	public static String createSharedSecretToken(String userName, Date loginTime, String sharedSecret) {
		
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException ex) {
			throw new SanityCheckException("Md5 not supported", ex);
		}
				
		String input = userName + "!" + formatTime(loginTime) + "!" + sharedSecret;
		byte[] digestBytes = digest.digest(input.getBytes());
				
		return toHexString(digestBytes);
		
	}
	
	public static String getPrettyTimeString( double seconds ) {
		
		int whole = (int) Math.floor(seconds);
		
		float mmin = Math.round(whole / 60f * 100) / 100f;
		int epochs = Math.round(mmin * 3);
		
		int ms = (int) (((double) Math.round( (seconds - whole) * 1000 )) / 1000);
		int hours = whole / 3600;
		whole = whole % 3600;
		int minutes = whole / 60;
		whole = whole % 60;
		
		StringBuilder sb = new StringBuilder();
		if( hours > 0 ) {
			sb.append( hours ).append( " h " );
		}
		if( hours > 0 || minutes > 0 ) {
			sb.append( minutes ).append( " m " );
		}
		sb.append( whole ).append( " s" );
		if( ms > 0 ) {
			sb.append( " " ).append( ms ).append( " ms " );
		}
		
		sb.append(" (").append( mmin ).append(" min") ;
		sb.append(" / "+epochs).append(" epochs)");
		
		return sb.toString();
	}
	
	public static double sqr( double x ) {
		return ( x*x );
	}
	
	public static int RGBToInteger(int red,int green,int blue) {
		return 0x00000000|(red<<16)|(green<<8)|blue;
	}

	public static boolean validateFqClassName(String fqClassName) {
		if( fqClassNamePattern == null ) {
			fqClassNamePattern = Pattern.compile("^[a-zA-Z0-9_][a-zA-Z0-9$._]*$");
		}
		return fqClassNamePattern.matcher(fqClassName).matches();
	}	
	
	public static String now(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	
}
