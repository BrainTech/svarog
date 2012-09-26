package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.exceptions.OpenbciConnectionException;
import org.signalml.app.worker.monitor.messages.Message;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.Netstring;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;
import org.signalml.util.FormatUtils;

/**
 * This helper is used to send and receive messages from OpenBCI.
 *
 * @author Piotr Szachewicz
 */
public class Helper {

	protected static Logger logger = Logger.getLogger(Helper.class);

	private static Socket socket;
	private static boolean cancelled;

	/**
	 * The socket timeout used by this helper by default.
	 */
	public static final int DEFAULT_RECEIVE_TIMEOUT = 10000;

	/**
	 * The constant holding a value for an infinite timeout.
	 */
	public static final int INFINITE_TIMEOUT = 0;

	public static String getOpenBCIIpAddress() {
		return SvarogApplication.getApplicationConfiguration().getOpenbciIPAddress();
	}

	public static int getOpenbciPort() {
		return SvarogApplication.getApplicationConfiguration().getOpenbciPort();
	}

	public static Message sendRequestAndParseResponse(Message request, String destinationIP, int destinationPort, MessageType awaitedMessageType) throws OpenbciCommunicationException {
		String responseString = sendRequest(request, destinationIP, destinationPort, DEFAULT_RECEIVE_TIMEOUT);
		MessageParser.checkIfResponseIsOK(responseString, awaitedMessageType);

		return MessageParser.parseMessageFromJSON(responseString, awaitedMessageType);
	}
	public static synchronized String sendRequest(Message request, String destinationIP,
			 int destinationPort) throws OpenbciCommunicationException {
		return sendRequest(request, destinationIP, destinationPort);
	}

	public static synchronized String sendRequest(Message request, String destinationIP,
			 int destinationPort, int timeout) throws OpenbciCommunicationException {

		try {
			return Helper.sendRequestWithoutHandlingExceptions(request, destinationIP, destinationPort, timeout);
		} catch (OpenbciCommunicationException ex) {
			if (cancelled) {
				//cancelling receving results in throwing this exception
				//so in this case we should ignore this exception.
				return null;
			}
			else {
				ex.printStackTrace();
				throw ex;
			}
		}
	}

	private static synchronized String sendRequestWithoutHandlingExceptions(Message request, String destinationIP,
			 int destinationPort, int timeout) throws OpenbciCommunicationException {

		createSocket(destinationIP, destinationPort, timeout);

		try {
			sendMessage(request);
		} catch (IOException e) {
			e.printStackTrace();
			throw new OpenbciCommunicationException(_("I/O error occurred while writing to socket."));
		}

		String response;
		try {
			response = receiveResponse();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			throw new OpenbciCommunicationException(_("Socket timeout exceeded while waiting for response"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new OpenbciCommunicationException(_("I/O error occurred while reading from socket."));
		}

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new OpenbciCommunicationException(_("I/O error occurred while closing the socket."));
		}

		return response;
	}

	private static void createSocket(String destinationIP, int destinationPort, int timeout) throws OpenbciCommunicationException {
		cancelled = false;
		try {
			socket = new Socket(destinationIP, destinationPort);
			socket.setSoTimeout(timeout);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			String message = _R("Could not connect to {0}:{1}", destinationIP, FormatUtils.formatNoGrouping(destinationPort));
			throw new OpenbciConnectionException(message, destinationIP, destinationPort);
		} catch (ConnectException e) {
			e.printStackTrace();
			String message = _R("Could not connect to {0}:{1} ({2})", destinationIP, FormatUtils.formatNoGrouping(destinationPort), e.getMessage());
			throw new OpenbciConnectionException(message, destinationIP, destinationPort);
		} catch (IOException e) {
			e.printStackTrace();
			throw new OpenbciCommunicationException(_("I/O exception while creating a socket."));
		}
	}

	private static void sendMessage(Message request) throws IOException {
		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		Netstring netstring = new Netstring(request);
		logger.debug("Sending message from "
				+ socket.getLocalAddress() + ":" + socket.getLocalPort()
				+ " to " + socket.getInetAddress() + ":" + socket.getPort()
				+ ": " + netstring);
		writer.println(netstring);
	}

	private static String receiveResponse() throws SocketTimeoutException, IOException, OpenbciCommunicationException {
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		StringBuilder stringBuilder = new StringBuilder();

		String line;
		do {
			line = in.readLine();
			if (line != null)
				stringBuilder.append(line);
		} while (line != null);

		String response = stringBuilder.toString();
		if (response == null || response.isEmpty())
			throw new OpenbciCommunicationException(_("Received an empty response from openBCI!"));

		Netstring responseNetstring = new Netstring();
		responseNetstring.parseNetstring(response);
		logger.debug("Got response: " + responseNetstring);

		return responseNetstring.getData();
	}

	/**
	 * Cancels receiving all messages that this Helper is waiting for.
	 */
	public static void cancelReceiving() {
		cancelled = true;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
