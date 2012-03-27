package org.signalml.app.worker.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.worker.monitor.messages.Message;
import org.signalml.app.worker.monitor.messages.Netstring;

public class Helper {

	protected static Logger logger = Logger.getLogger(Helper.class);

	public static final int RECEIVE_TIMEOUT_MS = 6000;

	protected static ApplicationConfiguration getApplicationConfiguration() {
		return SvarogApplication.getApplicationConfiguration();
	}

	public static String getOpenBCIIpAddress() {
		return getApplicationConfiguration().getOpenbciIPAddress();
	}

	public static int getOpenbciPort() {
		return getApplicationConfiguration().getOpenbciPort();
	}

	public static String sendRequest(Message request, String destinationIP,
			int destinationPort) throws SocketTimeoutException, IOException {
		Socket socket = new Socket(destinationIP, destinationPort);
		socket.setSoTimeout(RECEIVE_TIMEOUT_MS);

		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		Netstring netstring = new Netstring(request);
		logger.debug("Sending message to " + destinationIP
				+ ":" + destinationPort + ": " + netstring);
		writer.println(netstring);

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String response = in.readLine();
		if (response == null)
			return null;

		Netstring responseNetstring = new Netstring();
		responseNetstring.parseNetstring(response);
		logger.debug("Got response: " + responseNetstring);

		return responseNetstring.getData();
	}
}
