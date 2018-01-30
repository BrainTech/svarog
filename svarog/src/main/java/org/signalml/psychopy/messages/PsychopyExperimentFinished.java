package org.signalml.psychopy.messages;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.MessageType;

public class PsychopyExperimentFinished extends BaseMessage{
	@JsonProperty("created_files")
	public String[] createdFiles;

}
