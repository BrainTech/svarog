package org.signalml.psychopy.messages;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.MessageType;

public class RunPsychopyExperiment extends BaseMessage {
	@JsonProperty("script_path")
	private String experimentPath;

	@JsonProperty("output_path_prefix")
	private String outputDirectoryPath;

	@JsonIgnore
	public RunPsychopyExperiment(
			String sender,
			String experimentPath,
			String outputDirectoryPath
	) {
		super(MessageType.RUN_PSYCHOPY_EXPERIMENT);
		setSender(sender);
		this.experimentPath=experimentPath;
		this.outputDirectoryPath=outputDirectoryPath;
	}

}
