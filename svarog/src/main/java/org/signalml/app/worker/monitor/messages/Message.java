package org.signalml.app.worker.monitor.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Message {

	@JsonIgnore
	protected static final Logger logger = Logger.getLogger(Message.class);

	@JsonProperty("sender_ip")
	private String senderIp = "";
	private MessageType type;
	private String sender = "";
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

}
