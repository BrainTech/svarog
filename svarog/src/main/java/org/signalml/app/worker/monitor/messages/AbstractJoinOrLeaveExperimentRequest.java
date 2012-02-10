package org.signalml.app.worker.monitor.messages;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;

public abstract class AbstractJoinOrLeaveExperimentRequest extends Message {

	@JsonProperty("peer_id")
	private String peerId;

	@JsonProperty("peer_type")
	private String peerType;
	
	@JsonProperty("path")
	private String path;

	public AbstractJoinOrLeaveExperimentRequest(MessageType type, ExperimentDescriptor experiment) {
		super(type);
		this.peerType = "obci_peer";
		this.path = experiment.getPath();
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public String getPeerType() {
		return peerType;
	}

	public void setPeerType(String peerType) {
		this.peerType = peerType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
