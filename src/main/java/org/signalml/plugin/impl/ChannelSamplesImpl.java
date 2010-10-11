/**
 * 
 */
package org.signalml.plugin.impl;

import org.signalml.plugin.export.signal.ChannelSamples;

/**
 * This class holds samples of the signal for a given channel.
 * Contains also additional information, such as index of the channel,
 * label and sampling frequency.
 * @author Marcin Szumski
 *
 */
public class ChannelSamplesImpl implements ChannelSamples {
	
	/**
	 * An array with samples of the signal in the channel.
	 */
	private double[] samples;
	
	/**
	 * the index of the channel
	 */
	private int channelNumber;
	
	/**
	 * the number of samples per second
	 */
	private float samplingFrequency;
	
	/**
	 * the name (label) of the channel
	 */
	private String name;

	/**
	 * Constructor.
	 * @param samples an array of signal samples
	 * @param number the number (index) of the channel
	 * @param frequency the number of samples per second
	 * @param name the name (label) of the channel
	 */
	public ChannelSamplesImpl(double[] samples, int number, float frequency, String name){
		this.samples = samples;
		channelNumber = number;
		samplingFrequency = frequency;
		this.name = name;
	}
	
	/**
	 * Sets the number of the channel.
	 * @param channelNumber the number of the channel
	 */
	public void setChannelNumber(int channelNumber) {
		this.channelNumber = channelNumber;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.ChannelSamples#getChannelNumber()
	 */
	@Override
	public int getChannelNumber() {
		return channelNumber;
	}

	/**
	 * Sets the samples in the form of doubles.
	 * @param samples the samples in the form of doubles
	 */
	public void setSamples(double[] samples) {
		this.samples = samples;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.ChannelSamples#getSamples()
	 */
	@Override
	public double[] getSamples() {
		return samples;
	}

	/**
	 * Sets the sampling frequency.
	 * @param samplingFrequency the number of samples per second
	 */
	public void setSamplingFrequency(float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.ChannelSamples#getSamplingFrequency()
	 */
	@Override
	public float getSamplingFrequency() {
		return samplingFrequency;
	}

	/**
	 * Sets the name of the channel.
	 * @param name the name of the channel
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.ChannelSamples#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	
}
