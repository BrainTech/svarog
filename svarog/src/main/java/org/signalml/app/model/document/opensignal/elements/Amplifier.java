package org.signalml.app.model.document.opensignal.elements;

import java.util.ArrayList;
import java.util.List;
import org.signalml.app.worker.monitor.messages.AmplifierType;

public class Amplifier {

	private String name;
	private int samplesPerPacket;
	private List<Double> samplingFrequencies;
	private List<AmplifierChannel> channels;

	private AmplifierType amplifierType;
	private Object additionalParameters;

	public Amplifier(Amplifier amp) {
		copyFromPreset(amp);
	}

	public void copyFromPreset(Amplifier presetAmplifier) {
		this.name = presetAmplifier.name;
		this.samplesPerPacket = presetAmplifier.samplesPerPacket;

		this.samplingFrequencies = new ArrayList<Double>();
		for (Double samplingFrequency: presetAmplifier.getSamplingFrequencies())
			this.samplingFrequencies.add(samplingFrequency);

		this.channels = new ArrayList<AmplifierChannel>();
		for (AmplifierChannel channel: presetAmplifier.getChannels())
			this.channels.add(new AmplifierChannel(channel));

		//watch out - not everything is copied - e.g. - additionalParameters
		//need to stay in the previous version.
	}

	public Amplifier() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Double> getSamplingFrequencies() {
		return samplingFrequencies;
	}

	public void setSamplingFrequencies(
		List<Double> availableSamplingFrequencies) {
		this.samplingFrequencies = availableSamplingFrequencies;
	}

	public List<AmplifierChannel> getChannels() {
		return channels;
	}

	public void setChannels(List<AmplifierChannel> channels) {
		this.channels = channels;
	}

	public int getSamplesPerPacket() {
		return samplesPerPacket;
	}

	public void setSamplesPerPacket(int samplesPerPacket) {
		this.samplesPerPacket = samplesPerPacket;
	}

	public List<AmplifierChannel> getSelectedChannels() {
		List<AmplifierChannel> selectedChannels = new ArrayList<AmplifierChannel>();
		for (AmplifierChannel channel: this.getChannels()) {
			if (channel.isSelected())
				selectedChannels.add(channel);
		}
		return selectedChannels;
	}

	public String[] getSelectedChannelsLabels() {
		List<String> channelLabels = new ArrayList<String>();
		for (AmplifierChannel channel: getSelectedChannels()) {
			channelLabels.add(channel.getLabel());
		}
		return channelLabels.toArray(new String[0]);
	}

	public Object getAdditionalParameters() {
		return additionalParameters;
	}
	public void setAdditionalParameters(Object additionalParameters) {
		this.additionalParameters = additionalParameters;
	}

	public AmplifierType getAmplifierType() {
		return amplifierType;
	}
	public void setAmplifierType(AmplifierType amplifierType) {
		this.amplifierType = amplifierType;
	}

}
