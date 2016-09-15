package org.signalml.peer;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.zeromq.ZMQ;

/**
 * Object communicating with OBCI using ZMQ.
 * Peer instance has to be created, specifying peer ID and broker addresses,
 * and then {@link #connect} should be called. Without {@link #connect} call,
 * communication methods will not work. As soon as peer is not needed anymore,
 * {@link #shutdown} should be called.
 */
public class Peer {

	private static final Logger logger = Logger.getLogger(Peer.class);

	private final String peerId;
	private final BrokerInfo brokerInfo;
	private final ZMQ.Context context;

	private final ZMQ.Socket sub, rep; //, pub;

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
		sub = context.socket(ZMQ.SUB);
		rep = context.socket(ZMQ.REP);
		//pub = context.socket(ZMQ.PUB);
	}

	/**
	 * Send request to broker and return its answer.
	 *
	 * @param message  message to be sent to the broker
	 * @return  response from the broker
	 * @throws CommunicationException if communication fails
	 */
	public final Message askBroker(Message message) throws CommunicationException {
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
	public final Message askPeer(Message message, String repURL) throws CommunicationException {
		try (ZMQ.Socket req = context.socket(ZMQ.REQ)) {
			req.connect(repURL);

			req.send(message.getHeader(), ZMQ.SNDMORE);
			req.send(message.getData());

			String header = req.recvStr(); // no way to check for SNDMORE flag
			byte[] data = req.recv();
			Message response = Message.parse(header, data);
			if (!response.type.equals(message.type+"_RESPONSE")) {
				throw new CommunicationException("unexpected response "+response.type);
			}
			return response;
		}
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
		if (address.endsWith(":*")) {
			address = address.substring(0, address.length() - 2);
			int port = socket.bindToRandomPort(address);
			address += ":" + port;
		} else {
			socket.bind(address);
		}
		return address;
	}

	/**
	 * Connect this peer to the broker.
	 * This method performs HELLO initialization, but does not automatically
	 * subscribe to any messages. Therefore, to receive any messages,
	 * {@link #subscribe} must be call afterwards.
	 *
	 * @throws CommunicationException
	 */
	public void connect() throws CommunicationException {
		String repURL = bindSocket(rep, brokerInfo.repURLs[0]);

		try {
			JSONObject helloJSON = new JSONObject();
			helloJSON.put("broker_url", brokerInfo.brokerURL);
			helloJSON.put("peer_url", repURL);
			Message hello = new Message(Message.BROKER_HELLO, peerId, helloJSON.toString().getBytes());

			Message response = askBroker(hello);

			JSONObject responseJSON = new JSONObject(new String(response.data));
			String xpubURL = responseJSON.getString("xpub_url");
			//String xsubURL = responseJSON.getString("xsub_url");
			//pub.connect(xsubURL);
			sub.connect(xpubURL);

		} catch (JSONException ex) {
			throw new CommunicationException("JSON error in peer", ex);
		}
	}

	/**
	 * Wait for the message, and receive it when it's available.
	 *
	 * @return received message, or NULL on ZMQ error
	 * @throws CommunicationException  if received message is invalid
	 */
	public Message receive() throws CommunicationException {
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
	public Message receive(int timoutMillis) throws CommunicationException {
		sub.setReceiveTimeOut(timoutMillis);
		String header = sub.recvStr(); // no way to check for SNDMORE flag
		if (header == null) {
			return null;
		}
		sub.setReceiveTimeOut(-1);
		byte[] data = sub.recv();
		return Message.parse(header, data);
	}

	/**
	 * Close all this peers' sockets and free ZMQ resources.
	 * Peer instance cannot be used after calling this method.
	 */
	public void shutdown() {
		sub.close();
		rep.close();
		//pub.close();
		context.term();
	}

	/**
	 * Subscribe to given message type. Calling this method will make
	 * specified messages appear as results to {@link #receive}.
	 *
	 * @param type  message type
	 */
	public void subscribe(String type) {
		sub.subscribe(type.getBytes());
	}

}
