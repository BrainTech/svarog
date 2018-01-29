package org.signalml.psychopy.messages;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.MessageType;

public class PsychopyExperimentFinished extends BaseMessage{

	@JsonIgnore
	public PsychopyExperimentFinished(String sender) {
		super(MessageType.PSYCHOPY_EXPERIMENT_FINISHED);
		setSender(sender);
	}

}
