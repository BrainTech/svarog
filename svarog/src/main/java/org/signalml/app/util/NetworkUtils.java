package org.signalml.app.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * This class contains methods for operations on network addresses etc.
 *
 * @author Piotr Szachewicz
 */
public class NetworkUtils {

	/**
	 * Returns the network mask for the prefix length specified.
	 * E.g. - for maskLength = 24, this will return 255.255.255.0.
	 * @param maskLength
	 * @return
	 */
	public static InetAddress getIPv4Mask(int maskLength) {

		try {
			// Since this is for IPv4, it's 32 bits, so set the sign value of
			// the int to "negative"...
			int shiftby = (1 << 31);
			// For the number of bits of the prefix -1 (we already set the sign
			// bit)
			for (int i = maskLength - 1; i > 0; i--) {
				// Shift the sign right... Java makes the sign bit sticky on a
				// shift... So no need to "set it back up"...
				shiftby = (shiftby >> 1);
			}
			// Transform the resulting value in xxx.xxx.xxx.xxx format
			String maskString = Integer.toString((shiftby >> 24) & 255) + "." + Integer.toString((shiftby >> 16) & 255) + "." + Integer.toString((shiftby >> 8) & 255) + "." + Integer.toString(shiftby & 255);

			return InetAddress.getByName(maskString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static InetAddress getSubnetAddress(InetAddress address, int maskLength) throws UnknownHostException {
		InetAddress mask = NetworkUtils.getIPv4Mask(maskLength);

		byte[] addressBytes = address.getAddress();
		byte[] maskBytes = mask.getAddress();

		byte[] subnetBytes = new byte[addressBytes.length];

		for (int i = 0; i < addressBytes.length; i++) {
			subnetBytes[i] = (byte) (addressBytes[i] & maskBytes[i]);
		}

		InetAddress subnetAddress = InetAddress.getByAddress(subnetBytes);
		return subnetAddress;
	}

	public static boolean areAddressesInTheSameSubnet(InetAddress address1, InetAddress address2, int maskLength) throws UnknownHostException {
		InetAddress subnet1 = getSubnetAddress(address1, maskLength);
		InetAddress subnet2 = getSubnetAddress(address2, maskLength);

		return subnet1.equals(subnet2);
	}

	public static boolean isAddressIPv4(InetAddress address) {
		return !address.getHostAddress().contains(":");
	}

	public static int getFreePortNumber() throws IOException {
		ServerSocket server = new ServerSocket(0);
		int port = server.getLocalPort();
		server.close();

		return port;
	}
}
