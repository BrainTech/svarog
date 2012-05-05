package org.signalml.app.worker.monitor.messages;

import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonProperty;

public class GetPeerParametersValuesResponse extends Message {

	@JsonProperty("peer_id")
	private String peerId;

	@JsonProperty("param_values")
	private HashMap<String, Object> parameters;

	public GetPeerParametersValuesResponse() {
		super(MessageType.GET_PEER_PARAMETERS_VALUES_RESPONSE);
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public HashMap<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Returns if the message contains enough data to deduce
	 * that the experiment has started.
	 * @return
	 */
	public boolean isAmplifierStarted() {
		if (parameters == null)
			return false;

		/*
		 * e.g. channel_gains field should be filled if the
		 * experiment launching has been completed.
		 */
		String channelGains = (String) parameters.get("channel_gains");
		return !channelGains.isEmpty();
	}

}
