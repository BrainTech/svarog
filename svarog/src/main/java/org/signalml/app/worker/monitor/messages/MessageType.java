package org.signalml.app.worker.monitor.messages;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.signalml.app.worker.monitor.messages.parsing.MessageTypeDeserializer;
import org.signalml.app.worker.monitor.messages.parsing.MessageTypeSerializer;

@JsonSerialize(using=MessageTypeSerializer.class)
@JsonDeserialize(using=MessageTypeDeserializer.class)
public enum MessageType {

	PING("ping", Message.class),
	PONG("pong", Message.class),

	FIND_EEG_EXPERIMENTS_REQUEST("find_eeg_experiments", FindEEGExperimentsRequest.class),
	EEG_EXPERIMENTS_RESPONSE("eeg_experiments", null),

	FIND_EEG_AMPLIFIERS_REQUEST("find_eeg_amplifiers", FindEEGAmplifiersRequest.class),
	EEG_AMPLIFIERS_RESPONSE("eeg_amplifiers", null),

	START_EEG_SIGNAL_REQUEST("start_eeg_signal", StartEEGSignalRequest.class),
	START_EEG_SIGNAL_RESPONSE("starting_experiment", StartEEGSignalResponse.class),

	KILL_EXPERIMENT_REQUEST("kill_experiment", KillExperimentRequest.class),

	GET_EXPERIMENT_CONTACT_REQUEST("get_experiment_contact", GetExperimentContactRequest.class),
	GET_EXPERIMENT_CONTACT_RESPONSE("experiment_contact", GetExperimentContactResponse.class),

	JOIN_EXPERIMENT_REQUEST("join_experiment", JoinExperimentRequest.class),
	LEAVE_EXPERIMENT_REQUEST("leave_experiment", LeaveExperimentRequest.class),

	CAMERA_CONTROL_REQUEST("cam_control_msg", CameraControlRequest.class),

	REQUEST_OK_RESPONSE("rq_ok", RequestOKResponse.class),
	REQUEST_ERROR_RESPONSE("rq_error", RequestErrorResponse.class);

	protected static final Logger logger = Logger.getLogger(MessageType.class);

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
			logger.error("", e);
		} catch (JsonMappingException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
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
