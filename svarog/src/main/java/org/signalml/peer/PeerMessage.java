package org.signalml.peer;

/**
 * Basic data structure to be send and received using Peer class.
 * Message are sent and received as multipart ZeroMQ messages, with header
 * as first part and binary data as second part.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class PeerMessage {

	// definitions of message types, the same as in OBCI
	// (we cannot use the enum, unless we declare all *_RESPONSE types as well)

	public static final String AMPLIFIER_SIGNAL_MESSAGE = "AMPLIFIER_SIGNAL_MESSAGE";
	public static final String BROKER_HELLO = "BROKER_HELLO";
	public static final String TAG = "TAG";

	public static final String SAVE_VIDEO = "SAVE_VIDEO";
	public static final String SAVE_VIDEO_ERROR = "SAVE_VIDEO_ERROR";
	public static final String SAVE_VIDEO_OK = "SAVE_VIDEO_OK";
	public static final String FINISH_SAVING_VIDEO = "FINISH_SAVING_VIDEO";
	public static final String SAVE_VIDEO_DONE = "SAVE_VIDEO_DONE";

	/**
	 * Message type.
	 */
	public final String type;

	/**
	 * Message subtype, usually equal to peer ID.
	 */
	public final String subtype;

	/**
	 * Binary data contained in the message.
	 */
	public final byte[] data;

	/**
	 * Create a new message with given type, subtype and data.
	 *
	 * @param type  message type
	 * @param subtype  message subtype (usually equal to peer ID)
	 * @param data  binary data (reference to array will be stored in Message)
	 */
	public PeerMessage(String type, String subtype, byte[] data) {
		this.type = type;
		this.subtype = subtype;
		this.data = data;
	}

	/**
	 * Return header of the message, combined from type and subtype.
	 *
	 * @return  header of the message as String
	 */
	public String getHeader() {
		return this.type+'^'+this.subtype;
	}

	/**
	 * Return binary data of the message.
	 *
	 * @return  binary data stored in message
	 */
	public byte[] getData() {
		return this.data;
	}

	/**
	 * Create message from given header and binary data array.
	 *
	 * @param header  header of the message, combined from type and subtype
	 * @param data  binary data to be stored in message (as reference to array)
	 * @return  created Message instance
	 * @throws CommunicationException if message header is invalid
	 */
	public static PeerMessage parse(String header, byte[] data) throws CommunicationException {
		String[] headerParts = header.split("\\^");
		if (headerParts.length != 2) {
			throw new CommunicationException("invalid message header: " + header + String.format("%d", headerParts.length));
		}
		return new PeerMessage(headerParts[0], headerParts[1], data);
	}

}
