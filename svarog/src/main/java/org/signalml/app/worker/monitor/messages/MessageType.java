package org.signalml.app.worker.monitor.messages;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.signalml.app.worker.monitor.messages.parsing.MessageTypeDeserializer;
import org.signalml.app.worker.monitor.messages.parsing.MessageTypeSerializer;
import org.signalml.psychopy.messages.*;

@JsonSerialize(using=MessageTypeSerializer.class)
@JsonDeserialize(using=MessageTypeDeserializer.class)
public enum MessageType {

	PING("ping", LauncherMessage.class),
	PONG("pong", LauncherMessage.class),

	INCOMPLETE_TAG_MSG("INCOMPLETE_TAG", IncompleteTagMsg.class),
	TAG_MSG("TAG", TagMsg.class),
	
	BROKER_HELLO("BROKER_HELLO", BrokerHelloMsg.class),
	BROKER_HELLO_RESPONSE("BROKER_HELLO_RESPONSE", BrokerHelloResponseMsg.class),
	
	AMPLIFIER_SIGNAL_MESSAGE("AMPLIFIER_SIGNAL_MESSAGE", SignalMsg.class),
	
	START_SAVING_SIGNAL("svarog_start_saving_signal", StartSavingSignal.class),
	SAVING_SIGNAL_STARTING("svarog_saving_signal_starting", SavingSignalStarting.class),
	FINISH_SAVING_SIGNAL("svarog_finish_saving_signal", FinishSavingSignal.class),
	SAVING_SIGNAL_FINISHING("svarog_saving_signal_finishing", SavingSignalFinishing.class),
	SAVING_SIGNAL_ERROR("svarog_saving_signal_error", SavingSignalError.class),
	CHECK_SAVING_SIGNAL_STATUS("svarog_check_saving_signal_status", CheckSavingSignalStatus.class),
	SAVING_SIGNAL_STATUS("svarog_saving_signal_status", SavingSignalStatus.class),

	RUN_PSYCHOPY_EXPERIMENT("run_psychopy_experiment", RunPsychopyExperiment.class),
	PSYCHOPY_EXPERIMENT_STARTED("psychopy_experiment_started", PsychopyExperimentStarted.class),
	PSYCHOPY_EXPERIMENT_FINISHED("psychopy_experiment_finished", PsychopyExperimentFinished.class),
	PSYCHOPY_EXPERIMENT_ERROR("psychopy_experiment_error", PsychopyExperimentError.class),
	FINISH_PSYCHOPY_EXPERIMENT("finish_psychopy_experiment", FinishPsychopyExperiment.class),

	FIND_EEG_EXPERIMENTS_REQUEST("find_eeg_experiments", FindEEGExperimentsRequest.class),
	EEG_EXPERIMENTS_RESPONSE("eeg_experiments", EEGExperimentsMsg.class),

	FIND_EEG_AMPLIFIERS_REQUEST("find_eeg_amplifiers", FindEEGAmplifiersRequest.class),
	EEG_AMPLIFIERS_RESPONSE("eeg_amplifiers", EEGAmplifiersMsg.class),

	START_EEG_SIGNAL_REQUEST("start_eeg_signal", StartEEGSignalRequest.class),
	START_EEG_SIGNAL_RESPONSE("starting_experiment", StartEEGSignalResponse.class),

	KILL_EXPERIMENT_REQUEST("kill_experiment", KillExperimentRequest.class),
	KILL_EXPERIMENT_RESPONSE("kill_sent", KillExperimentResponse.class),

	OBCI_SERVER_CAPABILITIES_REQUEST("obci_server_capabilities_req", ObciServerCapabilitiesRequest.class),
	OBCI_SERVER_CAPABILITIES_RESPONSE("obci_server_capabilities", ObciServerCapabilitiesResponse.class),
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

	public Class getMessageClass() {
		return messageClass;
	}

	public void setMessageClass(Class messageClass) {
		this.messageClass = messageClass;
	}
}
