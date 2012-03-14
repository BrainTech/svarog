package org.signalml.app.model.document.opensignal;

import java.util.ArrayList;
import java.util.List;

import org.signalml.app.view.document.opensignal_old.elements.AmplifierChannel;

public class Amplifier {
	
	private String name;
	private int samplesPerPacket;
	private List<Float> samplingFrequencies;
	private List<AmplifierChannel> channels;

	private double amplifierNull;
	
	public Amplifier(Amplifier amp) {
		this.name = amp.name;
		this.samplesPerPacket = amp.samplesPerPacket;
		
		this.samplingFrequencies = new ArrayList<Float>();
		for (Float samplingFrequency: amp.getSamplingFrequencies())
			this.samplingFrequencies.add(samplingFrequency);
		
		this.channels = new ArrayList<AmplifierChannel>();
		for (AmplifierChannel channel: amp.getChannels())
			this.channels.add(new AmplifierChannel(channel));
		
		this.amplifierNull = amp.amplifierNull;
	}
	
	public Amplifier() {
	}

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

	public double getAmplifierNull() {
		return amplifierNull;
	}

	public void setAmplifierNull(double amplifierNull) {
		this.amplifierNull = amplifierNull;
	}

}
