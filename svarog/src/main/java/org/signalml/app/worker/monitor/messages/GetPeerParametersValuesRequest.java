package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonProperty;

public class GetPeerParametersValuesRequest extends Message {

	@JsonProperty("peer_id")
	private String peerId;

	public GetPeerParametersValuesRequest() {
		super(MessageType.GET_PEER_PARAMETERS_VALUES_REQUEST);
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

}
