package org.signalml.app.worker.monitor.zeromq;

import org.codehaus.jackson.annotate.JsonProperty;

public class FindEEGExperimentsRequest extends Message {

	@JsonProperty("client_push_address")
	private String clientPushAddress;
	
	public FindEEGExperimentsRequest(String clientPushAddress) {
		super(MessageType.FIND_EEG_EXPERIMENTS);
		this.clientPushAddress = clientPushAddress;
	}

	public String getClientPushAddress() {
		return clientPushAddress;
	}

	public void setClientPushAddress(String clientPushAddress) {
		this.clientPushAddress = clientPushAddress;
	}
	
	
}
