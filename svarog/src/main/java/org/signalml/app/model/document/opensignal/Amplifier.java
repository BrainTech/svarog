package org.signalml.app.model.document.opensignal;

import java.util.List;

import org.signalml.app.view.document.opensignal.elements.AmplifierChannel;

public class Amplifier {
	
	private String name;
	private List<Float> samplingFrequencies;
	private List<AmplifierChannel> channels;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Float> getSamplingFrequencies() {
		return samplingFrequencies;
	}

	public void setSamplingFrequencies(
			List<Float> availableSamplingFrequencies) {
		this.samplingFrequencies = availableSamplingFrequencies;
	}

	public List<AmplifierChannel> getChannels() {
		return channels;
	}

	public void setChannels(List<AmplifierChannel> channels) {
		this.channels = channels;
	}

}
