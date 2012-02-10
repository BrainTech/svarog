package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonIgnore;

public class ListAmplifierExperimentsResponse {

	@JsonIgnore
	private String amplifier_peer_info;

	public String getAmplifier_peer_info() {
		return amplifier_peer_info;
	}

	public void setAmplifier_peer_info(String amplifier_peer_info) {
		this.amplifier_peer_info = amplifier_peer_info;
	}
	
	
}
