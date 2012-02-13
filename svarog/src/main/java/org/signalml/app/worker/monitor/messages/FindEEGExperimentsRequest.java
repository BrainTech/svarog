package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonProperty;

public class FindEEGExperimentsRequest extends Message {

	@JsonProperty("client_push_address")
	private String clientPushAddress;
	
	@JsonProperty("checked_srvs")
	private String checkedSrvs;

	public FindEEGExperimentsRequest(String clientPushAddress) {
		super(MessageType.FIND_EEG_EXPERIMENTS_REQUEST);
		this.clientPushAddress = clientPushAddress;
		this.checkedSrvs = "";
	}

	public String getClientPushAddress() {
		return clientPushAddress;
	}

	public void setClientPushAddress(String clientPushAddress) {
		this.clientPushAddress = clientPushAddress;
	}	
	
}
