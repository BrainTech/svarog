/**
 * 
 */
package org.signalml.test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.signalml.exception.SanityCheckException;
import org.signalml.util.Util;

/**
 * @author oskar
 *
 */
public class SharedSecretAuthTest {

	
	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	
	private static String toHexString(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			hexString.append(HEX_CHARS[(bytes[i] & 0xF0) >> 4]).append(HEX_CHARS[bytes[i] & 0x0F]);
		}
		return hexString.toString();
	}
	
	private static String createSharedSecretToken(String userName, Date loginTime, String sharedSecret) {
		
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException ex) {
			throw new SanityCheckException("Md5 not supported", ex);
		}
				
		String input = userName + "!" + Util.formatTime(loginTime) + "!" + sharedSecret;
		byte[] digestBytes = digest.digest(input.getBytes());
				
		return toHexString(digestBytes);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String sharedSecret = "bigsecret";
		String userName = "oskar";
		List<String> loginTimeStringArray = new Vector<String>();


//		loginTimeStringArray.add("2008-07-24 12:14:01");
//		loginTimeStringArray.add("2008-08-24 12:14:01");
//		loginTimeStringArray.add("2008-07-24 13:24:43");
//		loginTimeStringArray.add("2008-08-24 13:24:43");
//		loginTimeStringArray.add("2008-07-24 14:04:16");
//		loginTimeStringArray.add("2008-08-24 14:04:16");
//		loginTimeStringArray.add("2008-07-24 15:19:56");
//		loginTimeStringArray.add("2008-08-24 15:19:56");
//		loginTimeStringArray.add("2008-07-24 16:11:34");
//		loginTimeStringArray.add("2008-08-24 16:11:34");
//		loginTimeStringArray.add("2008-07-24 12:14:48");
		loginTimeStringArray.add("2008-07-24 13:47:43");
		
		

		System.out.println("userName , loginTimeString, propperToken, sharedSecret = " + sharedSecret);

		for (String loginTimeString : loginTimeStringArray) {


			Date loginTime = null;

			try {
				loginTime = Util.parseTime(loginTimeString);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			String propperToken = createSharedSecretToken( userName, loginTime, sharedSecret );		


			System.out.println("" + userName + ", " + loginTimeString + ", " + propperToken);

//			System.out.println( "Authentication request userName [" + userName + "] loginTime [" + loginTime + "] token [" + propperToken + "]" );

		}
	}

}
