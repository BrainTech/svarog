package org.signalml.app.worker.monitor.zeromq;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Message {
	
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
	
	public String toJSON() {
		ObjectMapper mapper = new ObjectMapper(); 
		Map<String,Object> msgData = new HashMap<String,Object>();
		
		msgData.put("sender", "");
		msgData.put("sender_ip", "");
		msgData.put("receiver", "");
		msgData.put("type", "list_experiments");
		
		Writer listExpRequest = new StringWriter();
		try {
			mapper.writeValue(listExpRequest, msgData);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listExpRequest.toString();
	}
	
	public void fromJSON(String json) {
		ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> responseData = new HashMap<String,Object>();
        try {
			Map readValue = mapper.readValue(json, Map.class);
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
