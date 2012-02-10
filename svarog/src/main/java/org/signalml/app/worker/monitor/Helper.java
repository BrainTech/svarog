package org.signalml.app.worker.monitor;

import org.omg.CORBA.TIMEOUT;
import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.worker.monitor.messages.Message;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

public class Helper {
	
	public static final int RECEIVE_TIMEOUT_MS = 3000;

	public static String getObciServerAddressString() {
		
		if (SvarogApplication.getSharedInstance() == null)
			return "tcp://127.0.0.1:54654";
		
		ApplicationConfiguration config = SvarogApplication.getApplicationConfiguration();
		return getAddressString(config.getOpenBCIDaemonAddress(), config.getOpenBCIDaemonPort());
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
		socket.send(request.toJSON().getBytes(), 0);
		System.out.println("sent " + request.toJSON());

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
	
	public static boolean isObciServerResponding() {
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
