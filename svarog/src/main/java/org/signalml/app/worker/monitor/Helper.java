package org.signalml.app.worker.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.signalml.app.util.NetworkUtils;
import org.signalml.app.worker.monitor.messages.Message;
import org.signalml.app.worker.monitor.messages.Netstring;

public class Helper {

	protected static Logger logger = Logger.getLogger(Helper.class);

	public static final int RECEIVE_TIMEOUT_MS = 6000;
	private static String openbciInterfaceName = null;
	private static String openbciIpAddress = null;
	private static int openbciPort;

	public static String getOpenBCIIpAddress() {
		return openbciIpAddress;
	}

	public static int getOpenbciPort() {
		return openbciPort;
	}

	public static boolean wasOpenbciConfigFileLoaded() {
		return (openbciIpAddress != null);
	}

	public static void loadOpenbciConfigFile()
			throws InvalidFileFormatException, IOException {
		File homeDir = new File(System.getProperty("user.home"));
		File obciSettingsDir = new File(homeDir, ".obci");
		File mainConfigFile = new File(obciSettingsDir, "main_config.ini");

		Wini config = new Wini(mainConfigFile);

		openbciInterfaceName = config.get("server", "ifname");
		openbciPort = Integer.parseInt(config.get("server", "tcp_proxy_port"));

	}

	public static void findOpenbciIpAddress() throws SocketException {
		NetworkInterface netint = NetworkInterface
				.getByName(openbciInterfaceName);
		if (netint == null)
			throw new SocketException("The " + openbciInterfaceName
					+ " interface is not connected!");
		Enumeration<InetAddress> addrs = netint.getInetAddresses();

		for (InetAddress inetAddress : Collections.list(addrs)) {
			if (NetworkUtils.isAddressIPv4(inetAddress)) {
				openbciIpAddress = inetAddress.getHostAddress();
				return;
			}
		}
	}

	public static String sendRequest(Message request, String destinationIP,
			int destinationPort) throws SocketTimeoutException, IOException {
		Socket socket = new Socket(destinationIP, destinationPort);
		socket.setSoTimeout(RECEIVE_TIMEOUT_MS);

		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		Netstring netstring = new Netstring(request);
		logger.debug("Sending message " + netstring + " to " + destinationIP
				+ ":" + destinationPort);
		writer.println(netstring);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));

		String response = in.readLine();
		if (response == null)
			return null;

		Netstring responseNetstring = new Netstring();
		responseNetstring.parseNetstring(response);
		logger.debug("Got response: " + responseNetstring);

		return responseNetstring.getData();
	}
}
