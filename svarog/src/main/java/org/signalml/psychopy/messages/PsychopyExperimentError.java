package org.signalml.psychopy.messages;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.MessageType;

public class PsychopyExperimentError extends BaseMessage {
	@JsonProperty("details")
	private String details;

	@JsonIgnore
	public PsychopyExperimentError(String sender, String details) {
		super(MessageType.PSYCHOPY_EXPERIMENT_ERROR);
		setSender(sender);
		this.details = details;
	}

}
