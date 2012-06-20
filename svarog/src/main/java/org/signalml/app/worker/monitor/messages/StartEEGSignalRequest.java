package org.signalml.app.worker.monitor.messages;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.AmplifierChannel;

public class StartEEGSignalRequest extends Message {

	@JsonProperty("name")
	private String name;

	@JsonProperty("launch_file")
	private String launchFile;

	@JsonProperty("client_push_address")
	private String clientPushAddress = "";

	@JsonProperty("amplifier_params")
	private AmplifierParameters amplifierParameters;

	public StartEEGSignalRequest(ExperimentDescriptor experimentDescriptor) {
		super(MessageType.START_EEG_SIGNAL_REQUEST);

		name = createName(experimentDescriptor);
		launchFile = experimentDescriptor.getRecommendedScenario();
		amplifierParameters = new AmplifierParameters(experimentDescriptor);
	}

	protected String createName(ExperimentDescriptor experimentDescriptor) {
		name = "Svarog online - " + experimentDescriptor.getAmplifier().getName();
		return name;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLaunchFile() {
		return launchFile;
	}
	public void setLaunchFile(String launchFile) {
		this.launchFile = launchFile;
	}
	public String getClientPushAddress() {
		return clientPushAddress;
	}
	public void setClientPushAddress(String clientPushAddress) {
		this.clientPushAddress = clientPushAddress;
	}
	public AmplifierParameters getAmplifierParameters() {
		return amplifierParameters;
	}
	public void setAmplifierParameters(AmplifierParameters amplifierParameters) {
		this.amplifierParameters = amplifierParameters;
	}

}

class AmplifierParameters {

	@JsonProperty("channel_names")
	private String channelNames = "";

	@JsonProperty("active_channels")
	private String activeChannels = "";

	@JsonProperty("sampling_rate")
	private Float samplingFrequency;

	@JsonProperty("additional_params")
	private Object additionalParameters;

	public AmplifierParameters(ExperimentDescriptor experimentDescriptor) {
		samplingFrequency = experimentDescriptor.getSignalParameters().getSamplingFrequency();

		List<AmplifierChannel> selectedChannels = experimentDescriptor.getAmplifier().getSelectedChannels();
		for (int i = 0; i < selectedChannels.size(); i++) {
			AmplifierChannel channel = selectedChannels.get(i);

			if (AmplifierChannel.SPECIAL_CHANNEL_NAMES.contains(channel.getOriginalName()))
				activeChannels += channel.getOriginalName();
			else
				activeChannels += i;

			channelNames += channel.getLabel();

			if (i < selectedChannels.size()-1) {
				activeChannels += ";";
				channelNames += ";";
			}
		}

		this.additionalParameters = experimentDescriptor.getAmplifier().getAdditionalParameters();
	}

	public String getChannelNames() {
		return channelNames;
	}

	public void setChannelNames(String channelNames) {
		this.channelNames = channelNames;
	}

	public String getActiveChannels() {
		return activeChannels;
	}

	public void setActiveChannels(String activeChannels) {
		this.activeChannels = activeChannels;
	}

	public Float getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency(Float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	public void setAdditionalParameters(Object additionalParameters) {
		this.additionalParameters = additionalParameters;
	}
	public Object getAdditionalParameters() {
		return additionalParameters;
	}

}
