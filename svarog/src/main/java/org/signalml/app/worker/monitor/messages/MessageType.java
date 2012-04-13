package org.signalml.app.worker.monitor.messages;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.signalml.app.worker.monitor.messages.parsing.MessageParser;
import org.signalml.app.worker.monitor.messages.parsing.MessageTypeDeserializer;
import org.signalml.app.worker.monitor.messages.parsing.MessageTypeSerializer;

@JsonSerialize(using=MessageTypeSerializer.class)
@JsonDeserialize(using=MessageTypeDeserializer.class)
public enum MessageType {

	PING("ping", Message.class),
	PONG("pong", Message.class),

	FIND_EEG_EXPERIMENTS_REQUEST("find_eeg_experiments", FindEEGExperimentsRequest.class),
	EEG_EXPERIMENTS_RESPONSE("eeg_experiments", null),

	JOIN_EXPERIMENT_REQUEST("join_experiment", JoinExperimentRequest.class),
	LEAVE_EXPERIMENT_REQUEST("leave_experiment", LeaveExperimentRequest.class),

	REQUEST_OK_RESPONSE("rq_ok", RequestOKResponse.class),
	REQUEST_ERROR_RESPONSE("rq_error", RequestErrorResponse.class);

	private Class messageClass;
	private String messageCode;

	private MessageType(String messageCode, Class messageClass) {
		this.messageCode = messageCode;
		this.messageClass = messageClass;
	}

	public String getMessageCode() {
		return messageCode;
	}

	@Override
	public String toString() {
		return this.messageCode;
	}

	public static MessageType parseMessageTypeFromMessageCode(String code) {
		for (MessageType type: MessageType.values()) {
			if (type.getMessageCode().equalsIgnoreCase(code)) {
				return type;
			}
		}
		return null;
	}

	public static MessageType parseMessageTypeFromResponse(String response) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			HashMap<String,Object> map = mapper.readValue(response.getBytes(), new TypeReference<HashMap<String, Object>>() {});

			String msgTypeCode = (String) map.get("type");
			return MessageType.parseMessageTypeFromMessageCode(msgTypeCode);

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
		return null;
	}

	public Class getMessageClass() {
		return messageClass;
	}

	public void setMessageClass(Class messageClass) {
		this.messageClass = messageClass;
	}
}
