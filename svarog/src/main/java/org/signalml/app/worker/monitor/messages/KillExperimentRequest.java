package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonProperty;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;

public class KillExperimentRequest extends Message {

	@JsonProperty("strname")
	private String uuid;

	private boolean force;

	public KillExperimentRequest(ExperimentDescriptor experimentDescriptor) {
		super(MessageType.KILL_EXPERIMENT_REQUEST);
		this.uuid = experimentDescriptor.getId();
		this.force = false;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean getForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

}
