package org.signalml.app.worker.monitor.zeromq;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;

public class JoinExperimentsRequest extends Message {

	@JsonProperty("peer_id")
	private String peerId;

	@JsonProperty("peer_type")
	private String peerType;
	
	@JsonProperty("path")
	private String path;

	public JoinExperimentsRequest(ExperimentDescriptor experiment) {
		super(MessageType.JOIN_EXPERIMENT);
		this.peerId = "svarog" + (new Date().getMinutes()) + "" + (new Date().getSeconds());
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
