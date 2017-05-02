package org.signalml.app.worker.monitor.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.peer.PeerMessage;

public class Message {

	@JsonIgnore
	protected static final Logger logger = Logger.getLogger(Message.class);

	@JsonProperty("sender_ip")
	private String senderIp = "";
	@JsonIgnore
	private MessageType type;
	@JsonIgnore
	private String sender = "";
	@JsonProperty("receiver")
	private String receiver ="";

	public Message() {
	}

	public Message(MessageType type) {
		this.type = type;
	}

	public String getSenderIp() {
		return senderIp;
	}

	public void setSenderIp(String senderIp) {
		this.senderIp = senderIp;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String toString() {

		ObjectMapper mapper = new ObjectMapper();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			mapper.writeValue(os, this);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			logger.error("", e);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			logger.error("", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("", e);
		}

		return os.toString();
	}

	@JsonIgnore
	public String getHeader(){
		return type+"^"+sender+"^";	
	}
	@JsonIgnore
	public String getData(){
		return toString();
	}
	
	@JsonIgnore
	public List<String> serialize(){
		List<String> message = new ArrayList<>();
		message.add(getHeader());
		message.add(getData());
		return message;
	}
	
	@JsonIgnore
	public static Message deserialize(List<String> msg) throws OpenbciCommunicationException{
		String header = msg.get(0);
		String data = msg.get(1);
		MessageType type = parseMessageType(header);
		logger.debug("Parsed header: " + header);
		logger.debug("Parsing data: " + data);
		if (type != null){
			Message message = parseDataFromJSON(data, type);
			message.setSender(parseSender(header));
			return message;
		}
		else
			throw new OpenbciCommunicationException(_R("Unknown message type"));
	}
	
	@JsonIgnore
	public static Message deserialize(PeerMessage msg) throws OpenbciCommunicationException{
		String header = msg.getHeader();
		String data = new String(msg.getData());
		List<String> tagMsgString = new ArrayList();
		tagMsgString.add(header);
		tagMsgString.add(data);
		return deserialize(tagMsgString);
	}
	
	
	
	@JsonIgnore
	public static String[] parseHeader(String header)
	{
		return header.split("\\^");
	}
	
	@JsonIgnore
	public static MessageType parseMessageType(String header) {
		
		String msgTypeCode = parseHeader(header)[0];
		return MessageType.parseMessageTypeFromMessageCode(msgTypeCode);
	}
	
	@JsonIgnore
	public static String parseSender(String header) {
		try {
			return parseHeader(header)[1];
		}
		catch (java.lang.ArrayIndexOutOfBoundsException e) {
			return "";
		}
	}
	
	@JsonIgnore
	public static MessageType parseMessageType(List<String> msg) {
		return parseMessageType(msg.get(0));
	}
	
	
	@JsonIgnore
	public static Message parseDataFromJSON(String json, MessageType messageType) throws OpenbciCommunicationException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Message readMessage = (Message) mapper.readValue(json.getBytes(), messageType.getMessageClass());
			return readMessage;
			
		} catch (Exception e) {
			String msg = _R("An error occurred while parsing the JSON message ({0})", e.getStackTrace()[0]);
			logger.error(msg, e);
			throw new OpenbciCommunicationException(msg);
		}
	}
}

