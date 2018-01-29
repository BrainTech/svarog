package org.signalml.psychopy.messages;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.MessageType;

public class PsychopyExperimentStarted extends BaseMessage{

	@JsonIgnore
	public PsychopyExperimentStarted(String sender) {
		super(MessageType.PSYCHOPY_EXPERIMENT_STARTED);
		setSender(sender);
	}

}
