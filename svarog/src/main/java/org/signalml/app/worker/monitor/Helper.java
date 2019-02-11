package org.signalml.app.worker.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.LauncherMessage;
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
	
	private static ZMQ.Socket socket;
	private static ZMQ.Context context;
	private static boolean cancelled;

	/**
	 * The socket timeout used by this helper by default.
	 */
	public static final int DEFAULT_TIMEOUT = 10000;

	public static String getOpenBCIIpAddress() {
		return SvarogApplication.getApplicationConfiguration().getOpenbciIPAddress();
	}

	public static int getOpenbciPort() {
		return SvarogApplication.getApplicationConfiguration().getOpenbciPort();
	}

	
	public static BaseMessage sendRequestAndParseResponse(LauncherMessage request, String destinationIP, int destinationPort, MessageType awaitedMessageType) throws OpenbciCommunicationException {
		List<byte[]> response = sendRequest(request, destinationIP, destinationPort, DEFAULT_TIMEOUT);
		if (awaitedMessageType != null) {
			Helper.checkIfResponseIsOK(response, awaitedMessageType);
		}

		return BaseMessage.deserialize(response);
	}
	

	public static BaseMessage sendRequestAndGetResponse(BaseMessage request, String url) throws OpenbciCommunicationException {
		logger.debug("Sending request to: "+url);

		String IP;
		int port;
		IP = hostFromUrl(url);
		logger.debug("Got IP, " + IP);
		port = portFromUrl(url);
		logger.debug("Got port, " + port);

		logger.debug("Sending request to: "+IP+":"+ Integer.toString(port));

		List<byte[]> response = sendRequest(request, IP, port, DEFAULT_TIMEOUT);
		return BaseMessage.deserialize(response);
	}

	public static synchronized List<byte[]> sendRequest(BaseMessage request, String destinationIP,
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

	private static synchronized List<byte[]> sendRequestWithoutHandlingExceptions(BaseMessage request, String destinationIP,
			 int destinationPort, int timeout) throws OpenbciCommunicationException {
		createSocket(destinationIP, destinationPort, timeout);
		sendMessage(request);
		List<byte[]> response;
		response = receiveResponse();
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
		socket.setSendTimeOut(timeout);
		socket.setReceiveTimeOut(timeout);
		socket.connect(url);
		logger.debug("Socket: " + url+ " conected");
	}

	private static void sendMessage(BaseMessage request) throws OpenbciCommunicationException {
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
		

	private static List<byte[]> receiveResponse() throws OpenbciCommunicationException {
		List<byte[]> list = new ArrayList<byte[]>();
		byte[] response_header = socket.recv();
		logger.debug("Got header: " + response_header);
		byte[] response_data;
		if (socket.hasReceiveMore() && response_header != null ) {
			list.add(response_header);
			response_data = socket.recv();
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
	
	public static void checkIfResponseIsOK(List<byte[]> responseList, MessageType awaitedMessageType) throws OpenbciCommunicationException {

		MessageType type = LauncherMessage.parseMessageType(responseList.get(0));
		if (type == awaitedMessageType) {
			//it's ok - do nothing
		}
		else if (type == MessageType.REQUEST_ERROR_RESPONSE) {
			RequestErrorResponse msg = (RequestErrorResponse) LauncherMessage.deserialize(responseList);
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
	
	public static int portFromUrl(String url){
		
		String[] parts = url.split(":");
		return Integer.parseInt(parts[2]);
		
	}
	
	public static String hostFromUrl(String url){
		String[] parts = url.split(":");
		String hostname = parts[1];
		return hostname.substring(2);
	}

}
