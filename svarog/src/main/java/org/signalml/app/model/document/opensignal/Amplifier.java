package org.signalml.app.model.document.opensignal;

import java.util.List;

import org.signalml.app.view.document.opensignal.elements.AmplifierChannel;
import org.signalml.app.view.document.opensignal.elements.AmplifierChannels;

public class Amplifier {
	
	private String name;
	private List<Float> availableSamplingFrequencies;
	private AmplifierChannels channels;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Float> getAvailableSamplingFrequencies() {
		return availableSamplingFrequencies;
	}

	public void setAvailableSamplingFrequencies(
			List<Float> availableSamplingFrequencies) {
		this.availableSamplingFrequencies = availableSamplingFrequencies;
	}

	public AmplifierChannels getChannels() {
		return channels;
	}

	public void setChannels(AmplifierChannels channels) {
		this.channels = channels;
	}

}
