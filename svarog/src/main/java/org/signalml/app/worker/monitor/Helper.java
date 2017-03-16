package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.Message;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.RequestErrorResponse;
import org.zeromq.ZMQ;

/**
 * This helper is used to send and receive messages from OpenBCI.
 *
 * @author Piotr Szachewicz
 */
public class Helper {

	protected static Logger logger = Logger.getLogger(Helper.class);
	
	private static final int TIMEOUT = 2000; // milliseconds
	private static ZMQ.Socket socket;
	private static ZMQ.Context context;
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
		List<String> response = sendRequest(request, destinationIP, destinationPort, DEFAULT_RECEIVE_TIMEOUT);
		Helper.checkIfResponseIsOK(response, awaitedMessageType);

		return Message.deserialize(response);
	}

	public static synchronized List<String> sendRequest(Message request, String destinationIP,
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
				logger.error("", ex);
				throw ex;
			}
		}
	}

	private static synchronized List<String> sendRequestWithoutHandlingExceptions(Message request, String destinationIP,
			 int destinationPort, int timeout) throws OpenbciCommunicationException {

		createSocket(destinationIP, destinationPort, timeout);

		
		sendMessage(request);
	

		List<String> response;
		try {
			response = receiveResponse();
		} catch (SocketTimeoutException e) {
			logger.error("", e);
			throw new OpenbciCommunicationException(_("Socket timeout exceeded while waiting for response"));
		}
		socket.close();

		return response;
	}

	private static void createSocket(String destinationIP, int destinationPort, int timeout) throws OpenbciCommunicationException {
		cancelled = false;
		String destinationPortString = Integer.toString(destinationPort);
		String url = "tcp://"+destinationIP+":"+destinationPortString;
		logger.debug("Creating socket: " + url);
		context = ZMQ.context(1);
		socket = context.socket(ZMQ.REQ);
		socket.setLinger(0);
		socket.setHWM(100);
		socket.setSendTimeOut(TIMEOUT);
		socket.setReceiveTimeOut(TIMEOUT);
		socket.connect(url);
		logger.debug("Socket: " + url+ " conected");
		
	}

	private static void sendMessage(Message request) throws OpenbciCommunicationException {
		
		String header = request.getHeader();
		String data = request.getData();
		
		logger.debug("Sending message to OBCI: " + header + data);
		if(!socket.sendMore(header)){
			String msg = "Error while sending message header";
			logger.error(msg);
			throw new OpenbciCommunicationException(msg);
		}
		if(!socket.send(data)){
			String msg = "Error while sending message data";
			logger.error(msg);
			throw new OpenbciCommunicationException(msg);

		}
		
        
	}
		

	private static List<String> receiveResponse() throws SocketTimeoutException, OpenbciCommunicationException {
		List<String> list = new ArrayList<String>();
		String response_header = socket.recvStr();
		logger.debug("Got header: " + response_header);
		String response_data;
		if (socket.hasReceiveMore() && response_header != null ) {
			list.add(response_header);
			response_data = socket.recvStr();
			if (response_data == null)
				throw new OpenbciCommunicationException(_("Received an empty response from openBCI!"));
			else
				list.add(response_data);
			}
		
		else {
			throw new OpenbciCommunicationException(_("No response data is being sent!"));
		}
		
		logger.debug("Got response: " + response_header + response_data);
		
		return list;
	}
	
	public static void checkIfResponseIsOK(List<String> responseList, MessageType awaitedMessageType) throws OpenbciCommunicationException {

		MessageType type = Message.parseMessageType(responseList.get(0));
		if (type == awaitedMessageType) {
			//it's ok - do nothing
		}
		else if (type == MessageType.REQUEST_ERROR_RESPONSE) {
			RequestErrorResponse msg = (RequestErrorResponse) Message.deserialize(responseList);
			if (isNullOrEmpty(msg.getErrorCode()) && !isNullOrEmpty(msg.getDetails())) {
				throw new OpenbciCommunicationException(msg.getDetails());
			}
			throw new OpenbciCommunicationException(_R("Got request error from server (code: {0})", msg.getErrorCode()));
		}
		else {
			throw new OpenbciCommunicationException(_R("Got unexpected response from the server: {0},{1}" ,responseList.get(0), responseList.get(1)));
		}
	}

	private static boolean isNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}
	
	/**
	 * Cancels receiving all messages that this Helper is waiting for.
	 */
	public static void cancelReceiving() {
		cancelled = true;
	}

}
