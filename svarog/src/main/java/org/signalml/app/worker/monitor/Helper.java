package org.signalml.app.worker.monitor;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.util.NetworkUtils;
import org.signalml.app.worker.monitor.messages.Message;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class Helper {
	
	public static final int RECEIVE_TIMEOUT_MS = 6000;
	
	private static String openbciInterfaceName = null;
	private static String openbciIpAddress = null;
	private static int openbciPort;
	
	public static String getOpenbciIpAddress() {
		return openbciIpAddress;
	}
	
	public static boolean wasOpenbciConfigFileLoaded() {
		return (openbciIpAddress != null);
	}
	
	public static void loadOpenbciConfigFile() throws InvalidFileFormatException, IOException {
		File homeDir = new File(System.getProperty("user.home"));
		File obciSettingsDir = new File(homeDir, ".obci");
		File mainConfigFile = new File(obciSettingsDir, "main_config.ini");
		
		Wini config = new Wini(mainConfigFile);

		openbciInterfaceName = config.get("server", "ifname");
		openbciPort = Integer.parseInt(config.get("server", "port"));
	}
	
	public static void findOpenbciIpAddress() throws SocketException {
		NetworkInterface netint = NetworkInterface.getByName(openbciInterfaceName);
		if (netint == null)
			throw new SocketException("The " + openbciInterfaceName + " interface is not connected!");
		Enumeration<InetAddress> addrs = netint.getInetAddresses();

		for (InetAddress inetAddress : Collections.list(addrs)) {
			if (NetworkUtils.isAddressIPv4(inetAddress)) {
				openbciIpAddress = inetAddress.getHostAddress();
				return;
			}
		}
	}

	public static String getObciServerAddressString() {
		return getAddressString(openbciIpAddress, openbciPort);
	}
	
	public static String getAddressString(String ipAddress, int port) {		
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("tcp://");
		stringBuffer.append(ipAddress);
		stringBuffer.append(":");
		stringBuffer.append(port);
		
		return stringBuffer.toString();
	}
	
	public static String sendRequest(Message request, String destinationAddress) {

		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket socket = context.socket(ZMQ.REQ);
		
		ZMQ.Poller poller = context.poller();
		poller.register(socket, Poller.POLLIN);
		
		socket.connect(destinationAddress);
		socket.send(request.toString().getBytes(), 0);
		System.out.println("sent " + request.toString());

		if (poller.poll(getTimeOutLength()) > 0) {
			byte[] responseBytes = socket.recv(0);
			String response = new String(responseBytes);
			System.out.println("response: " + response);
			socket.close();
			return response;
		}
		else {
			System.out.println("No response - timeout!!");
			socket.close();
			return null;
		}
	}
	
	public static boolean isObciServerResponding() throws InvalidFileFormatException, IOException {
		Message request = new Message(MessageType.PING);
		String response = Helper.sendRequest(request, Helper.getObciServerAddressString());
		
		boolean responseOK = MessageParser.checkIfResponseIsOK(response, MessageType.PONG);
		return responseOK;
	}
	
	/**
	 * Returns the receive timeout for ZMQ sockets.
	 * 'Since ZeroMQ 3.0, the timeout parameter is in milliseconds,
	 * but prior to this the unit was microseconds' - that is
	 * why this method is needed. 
	 * @return receive timeout
	 */
	static int getTimeOutLength() {
		if (ZMQ.getFullVersion() < 30000)
			return RECEIVE_TIMEOUT_MS * 1000;
		return RECEIVE_TIMEOUT_MS;
	}

}
