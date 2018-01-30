package org.signalml.psychopy.messages;

import org.codehaus.jackson.annotate.JsonProperty;
import org.signalml.app.worker.monitor.messages.BaseMessage;

public class PsychopyExperimentError extends BaseMessage {
	@JsonProperty("details")
	public String details;

}
