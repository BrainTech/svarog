package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.worker.monitor.messages.Message;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.Netstring;
import org.signalml.app.worker.monitor.messages.RequestOKResponse;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;

public class Helper {

	protected static Logger logger = Logger.getLogger(Helper.class);

	public static final int RECEIVE_TIMEOUT_MS = 10000;

	protected static ApplicationConfiguration getApplicationConfiguration() {
		return SvarogApplication.getApplicationConfiguration();
	}

	public static String getOpenBCIIpAddress() {
		return getApplicationConfiguration().getOpenbciIPAddress();
	}

	public static int getOpenbciPort() {
		return getApplicationConfiguration().getOpenbciPort();
	}

	public static Message sendRequestAndParseResponse(Message request, String destinationIP, int destinationPort, MessageType awaitedMessageType) {
		String responseString = sendRequestAndHandleExceptions(request, destinationIP, destinationPort);

		if (!MessageParser.checkIfResponseIsOK(responseString, awaitedMessageType)) {
			return null;
		}

		return MessageParser.parseMessageFromJSON(responseString, awaitedMessageType);
	}

	public static String sendRequestToOpenBCI(Message request) throws SocketTimeoutException, IOException {
		return sendRequest(request, getOpenBCIIpAddress(), getOpenbciPort());
	}

	public static String sendRequestAndHandleExceptions(Message request, String destinationIP,
			int destinationPort) {
		String responseString = null;
		try {
			responseString = Helper.sendRequest(request, destinationIP, destinationPort);
		} catch (SocketTimeoutException ex) {
			ex.printStackTrace();
			Dialogs.showError(_("Socket timeout exceeded!"));
		} catch (ConnectException ex) {
			ex.printStackTrace();
			Dialogs.showError(_("Could not connect to the experiment!"));
		} catch (IOException ex) {
			ex.printStackTrace();
			Dialogs.showError(_("Error while IO operation on socket."));
		}
		return responseString;
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

		StringBuilder stringBuilder = new StringBuilder();

		String line = "";
		do {
			line = in.readLine();
			if (line != null)
				stringBuilder.append(line);
		} while (line != null);

		String response = stringBuilder.toString();
		if (response == null || response.isEmpty())
			return null;

		Netstring responseNetstring = new Netstring();
		responseNetstring.parseNetstring(response);
		logger.debug("Got response: " + responseNetstring);

		socket.close();

		return responseNetstring.getData();
	}
}
