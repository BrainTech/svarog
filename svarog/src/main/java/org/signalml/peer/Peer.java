package org.signalml.peer;

import org.apache.log4j.Logger;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.worker.monitor.Helper;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.BrokerHelloMsg;
import org.signalml.app.worker.monitor.messages.BrokerHelloResponseMsg;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 * Object communicating with OBCI using ZMQ.
 * Peer instance has to be created, specifying peer ID and broker addresses,
 * and then {@link #connect} should be called. Without {@link #connect} call,
 * communication methods will not work. As soon as peer is not needed anymore,
 * {@link #shutdown} should be called.
 */
public class Peer {

	private static final Logger logger = Logger.getLogger(Peer.class);

	/**
	 * Default High Water-Mark for sockets.
	 */
	private static final int HWM = 500;

	private final String peerId;
	private final BrokerInfo brokerInfo;
	private final ZMQ.Context context;

	private final ZMQ.Socket sub, rep, pub;

	private boolean closed;

	/**
	 * Create a new Peer instance and its sockets.
	 * To connect peer to broker, {@link #connect} must be called afterwards.
	 *
	 * @param peerId  ID for the new peer
	 * @param brokerInfo  {@link BrokerInfo} instance with broker addresses
	 */
	public Peer(String peerId, BrokerInfo brokerInfo) {
		this.peerId = peerId;
		this.brokerInfo = brokerInfo;

		context = ZMQ.context(1);
		sub = createSocket(ZMQ.SUB);
		rep = createSocket(ZMQ.REP);
		pub = createSocket(ZMQ.PUB);
		closed = false;
	}

	/**
	 * Send request to broker and return its answer.
	 *
	 * @param message  message to be sent to the broker
	 * @return  response from the broker
	 * @throws CommunicationException if communication fails
	 */
	public final BaseMessage askBroker(BaseMessage message) throws OpenbciCommunicationException {
		return askPeer(message, brokerInfo.brokerURL);
	}

	/**
	 * Send request to a given peer and return its answer.
	 *
	 * @param message  message to be sent to the peer
	 * @param repURL  URL address of peer's REP socket
	 * @return  response from the peer
	 * @throws CommunicationException if communication fails
	 */
	public final BaseMessage askPeer(BaseMessage message, String repURL) throws OpenbciCommunicationException {
			
		return Helper.sendRequestAndGetResponse(message, repURL);
		
	}

	/**
	 * Bind socket to a given address. It is not required that given address
	 * has specified port number (* can be used instead).
	 *
	 * @param socket  socket to connect
	 * @param address  TCP/IP URL
	 * @return actual address on which socket is bound
	 */
	private static String bindSocket(ZMQ.Socket socket, String address) {
		socket.bind(address);
		return (String) socket.base().getSocketOptx(zmq.ZMQ.ZMQ_LAST_ENDPOINT);
	}

	/**
	 * Create ZMQ socket of specified type. Set linger=0 and default HWM.
	 *
	 * @param type  socket type, e.g. ZMQ.REP
	 * @return newly created socket
	 */
	private ZMQ.Socket createSocket(int type) {
		ZMQ.Socket socket = context.socket(type);
		socket.setLinger(0);
		socket.setHWM(HWM);
		return socket;
	}

	/**
	 * Connect this peer to the broker.
	 * This method performs HELLO initialization, but does not automatically
	 * subscribe to any messages. Therefore, to receive any messages,
	 * {@link #subscribe} must be call afterwards.
	 *
	 * @throws CommunicationException
	 * @throws org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException
	 */
	public void connect() throws CommunicationException, OpenbciCommunicationException {
		String repURL = bindSocket(rep, brokerInfo.repURLs[0]);


		BrokerHelloMsg hello = new BrokerHelloMsg(repURL, brokerInfo.brokerURL);
		hello.setSender(peerId);
		BrokerHelloResponseMsg response = (BrokerHelloResponseMsg)askBroker(hello);

		pub.connect(response.getXsubUrl());
		sub.connect(response.getXpubUrl());
		
		
	}

	/**
	 * Check whether the socket has more message parts to receive.
	 *
	 * @param socket  socket to check
	 * @return true if more parts are coming, false otherwise
	 */
	private static boolean isRcvMore(ZMQ.Socket socket) {
		int rcvMore = (Integer) socket.base().getSocketOptx(zmq.ZMQ.ZMQ_RCVMORE);
		return rcvMore == 1;
	}

	/**
	 * Receive multi-part message from given socket.
	 * Message has two consist from exactly two parts.
	 *
	 * @param socket socket to receive message from
	 * @param timeoutMillis timeout in milliseconds, or -1 to wait forever
	 * @return new Message instance
	 * @throws CommunicationException if received message is invalid
	 */
	private static BaseMessage receiveFromSocket(ZMQ.Socket socket, int timeoutMillis) throws OpenbciCommunicationException {
		int previousTimeout = socket.getReceiveTimeOut();
		socket.setReceiveTimeOut(timeoutMillis);
		byte[] header = socket.recv();
		socket.setReceiveTimeOut(previousTimeout);
		if (header == null) {
			// timeout
			return null;
		}
		if (!isRcvMore(socket)) {
			throw new OpenbciCommunicationException(_("received invalid one-part message"));
		}
		byte[] data = socket.recv();
		if (isRcvMore(socket)) {
			throw new OpenbciCommunicationException(_("received message with more than two parts"));
		}
		return BaseMessage.deserialize(header, data);
	}

	/**
	 * Wait for the message, and receive it when it's available.
	 *
	 * @return received message, or NULL on ZMQ error
	 * @throws CommunicationException  if received message is invalid
	 */
	public BaseMessage receive() throws OpenbciCommunicationException {
		return receive(-1);
	}

	/**
	 * Wait for the message up to a given timeout, and receive it
	 * if it become available.
	 *
	 * @param timoutMillis  time-out in milliseconds
	 * @return received message, or NULL on ZMQ error or timeout
	 * @throws CommunicationException  if received message is invalid
	 */
	public BaseMessage receive(int timoutMillis) throws OpenbciCommunicationException {
		return receiveFromSocket(sub, timoutMillis);
	}

	public synchronized void publish(BaseMessage message) {
		if (this.closed){
			throw new ZMQException(_("sockets are closed"), 5);
		}
		try {
			pub.send(message.getHeader(), ZMQ.SNDMORE);
			pub.send(message.getData());
		} catch (ZMQException ex) {
			logger.error("could not publish message (header="+message.getHeader()+")", ex);
			throw ex;
		}
	}

	/**
	 * Close all this peers' sockets and free ZMQ resources.
	 * Peer instance cannot be used after calling this method.
	 */
	public void shutdown() {
		sub.close();
		rep.close();
		pub.close();
		this.closed = true;
		Thread closing = new Thread(() -> {
			context.term();
		});
		// if internal ZMQ thread raises an exception,
		// context.term() sometimes hangs forever,
		// therefore it must be closed asynchronously
		closing.setDaemon(true);
		closing.start();
		try {
			closing.join(1000);
		} catch (InterruptedException ex) {
			// interrupted
		}
	}

	/**
	 * Subscribe to given message type. Calling this method will make
	 * specified messages appear as results to {@link #receive}.
	 *
	 * @param type  message type
	 */
	public void subscribe(String type) {
		sub.subscribe(Converter.bytesFromString(type+'^'));
	}

}
