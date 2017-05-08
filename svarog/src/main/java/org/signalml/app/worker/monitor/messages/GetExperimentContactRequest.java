package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonProperty;

public class GetExperimentContactRequest extends LauncherMessage {

	@JsonProperty("strname")
	private String experimentUUID;

	public GetExperimentContactRequest(String experimentUUID) {
		super(MessageType.GET_EXPERIMENT_CONTACT_REQUEST);
		this.experimentUUID = experimentUUID;
	}

	public String getExperimentUUID() {
		return experimentUUID;
	}

	public void setExperimentUUID(String experimentUUID) {
		this.experimentUUID = experimentUUID;
	}

}
