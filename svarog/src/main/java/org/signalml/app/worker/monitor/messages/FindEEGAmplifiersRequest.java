package org.signalml.app.worker.monitor.messages;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class FindEEGAmplifiersRequest extends Message {

	@JsonProperty("client_push_address")
	private String clientPushAddress;

	@JsonProperty("checked_srvs")
	private String checkedSrvs;

	@JsonProperty("amplifier_types")
	private List<AmplifierType> amplifierTypes = new ArrayList<AmplifierType>();

	public FindEEGAmplifiersRequest() {
		super(MessageType.FIND_EEG_AMPLIFIERS_REQUEST);
		this.clientPushAddress = "";
		this.checkedSrvs = "";

		amplifierTypes.add(AmplifierType.USB);
		amplifierTypes.add(AmplifierType.BLUETOOTH);
		amplifierTypes.add(AmplifierType.VIRTUAL);
	}

	public FindEEGAmplifiersRequest(AmplifierType amplifierType) {
		super(MessageType.FIND_EEG_AMPLIFIERS_REQUEST);
		this.clientPushAddress = "";
		this.checkedSrvs = "";

		amplifierTypes.add(amplifierType);
	}

	public String getClientPushAddress() {
		return clientPushAddress;
	}

	public void setClientPushAddress(String clientPushAddress) {
		this.clientPushAddress = clientPushAddress;
	}

}
