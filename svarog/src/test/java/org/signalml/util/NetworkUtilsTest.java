package org.signalml.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;
import org.signalml.app.util.NetworkUtils;

import static org.signalml.SignalMLAssert.*;

public class NetworkUtilsTest {

	@Test
	public void testGetIPv4LocalNetMask() throws UnknownHostException {
		InetAddress mask = NetworkUtils.getIPv4Mask(24);
		assertEquals(mask.getHostAddress(), "255.255.255.0");

		mask = NetworkUtils.getIPv4Mask(23);
		assertEquals(mask.getHostAddress(), "255.255.254.0");

		mask = NetworkUtils.getIPv4Mask(22);
		assertEquals(mask.getHostAddress(), "255.255.252.0");
	}

	@Test
	public void testAddressessInTheSameSubnet() throws UnknownHostException {
		InetAddress address1 = InetAddress.getByName("192.168.1.1");
		InetAddress address2 = InetAddress.getByName("192.168.1.23");
		boolean sameSubnet = NetworkUtils.areAddressesInTheSameSubnet(address1, address2, 16);
		assertEquals(sameSubnet, true);

		address1 = InetAddress.getByName("192.167.1.1");
		address2 = InetAddress.getByName("192.168.1.23");
		sameSubnet = NetworkUtils.areAddressesInTheSameSubnet(address1, address2, 16);
		assertEquals(sameSubnet, false);

		address1 = InetAddress.getByName("192.168.129.11");
		address2 = InetAddress.getByName("192.168.255.23");
		sameSubnet = NetworkUtils.areAddressesInTheSameSubnet(address1, address2, 17);
		assertEquals(sameSubnet, true);

		address1 = InetAddress.getByName("192.168.129.11");
		address2 = InetAddress.getByName("192.168.55.23");
		sameSubnet = NetworkUtils.areAddressesInTheSameSubnet(address1, address2, 17);
		assertEquals(sameSubnet, false);

		address1 = InetAddress.getByName("192.168.129.11");
		address2 = InetAddress.getByName("192.168.255.23");
		sameSubnet = NetworkUtils.areAddressesInTheSameSubnet(address1, address2, 18);
		assertEquals(sameSubnet, false);
	}

	@Test
	public void testIsAddressAnIPv4Address() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("fe80:0:0:0:21e:65ff:fe94:8f12%3");
		assertEquals(false, NetworkUtils.isAddressIPv4(address));

		address = InetAddress.getByName("192.168.1.112");
		assertEquals(true, NetworkUtils.isAddressIPv4(address));
	}
}
