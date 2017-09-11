/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.worker.monitor.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;


public class BaseMessage {
	@JsonIgnore
	protected static final Logger logger = Logger.getLogger(LauncherMessage.class);

	
	@JsonIgnore
	private MessageType type;
	@JsonIgnore
	private String sender = "";

	public BaseMessage() {
	}

	public BaseMessage(MessageType type) {
		this.type = type;
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


	public String toString() {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);

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
	public static BaseMessage deserialize(List<byte[]> msg) throws OpenbciCommunicationException{
		byte[] header = msg.get(0);
		byte[] data = msg.get(1);
		return deserialize(header, data);
	}
	
	
	@JsonIgnore
	public BaseMessage deseralizeData(byte[] data, MessageType type) throws OpenbciCommunicationException{
		logger.debug("Parsing data: " + new String(data));
		BaseMessage message = parseDataFromJSON(data, type);
		return message;
	}
	
	@JsonIgnore
	public static BaseMessage deserialize(byte[] header, byte[] data) throws OpenbciCommunicationException{
		MessageType type = parseMessageType(header);
		if (type != null){
			try {
				BaseMessage message;
				Constructor msgconst;
				msgconst = type.getMessageClass().getConstructor();
				message = (BaseMessage) msgconst.newInstance();
				message = message.deseralizeData(data, type);
				message.setType(type);
				message.setSender(parseSender(header));
				return message;
			} catch (InstantiationException ex) {
				logger.error(ex);
			} catch (IllegalAccessException ex) {
				logger.error(ex);
			} catch (IllegalArgumentException ex) {
				logger.error(ex);
			} catch (InvocationTargetException ex) {
				logger.error(ex);
			} catch (NoSuchMethodException ex) {
				logger.error(ex);
			} catch (SecurityException ex) {
				logger.error(ex);
			}
			return null;
		}
		else
			throw new OpenbciCommunicationException(_R("Unknown message type"));
	}
	
	
	
	@JsonIgnore
	public static String[] parseHeader(byte[] header) throws OpenbciCommunicationException
	{
		try {
			return (new String(header,"UTF8")).split("\\^");
		} catch (UnsupportedEncodingException ex) {
			throw new OpenbciCommunicationException(_R("Unknown message chartype"));
		}
	}
	
	@JsonIgnore
	public static MessageType parseMessageType(byte[] header) throws OpenbciCommunicationException {
		
		String msgTypeCode = parseHeader(header)[0];
		return MessageType.parseMessageTypeFromMessageCode(msgTypeCode);
	}
	
	@JsonIgnore
	public static String parseSender(byte[] header) throws OpenbciCommunicationException {
		try {
			return parseHeader(header)[1];
		}
		catch (java.lang.ArrayIndexOutOfBoundsException e) {
			return "";
		}
	}
	
	
	@JsonIgnore
	public static BaseMessage parseDataFromJSON(byte[] json, MessageType messageType) throws OpenbciCommunicationException {
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			//TODO WORKS EXACTLY UP TO HERE
			BaseMessage readMessage = (BaseMessage) mapper.readValue(new String(json), messageType.getMessageClass());

			return readMessage;
			
		} catch (Exception e) {
			String msg = _R("An error occurred while parsing the JSON message ({0})", e.getStackTrace()[0]);
			logger.error(msg, e);
			throw new OpenbciCommunicationException(msg);
		}
	}
}
