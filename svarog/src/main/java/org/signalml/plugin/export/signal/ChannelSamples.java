package org.signalml.plugin.export.signal;

/**
 * This interface allows to return samples of the signal for a given channel.
 * Provides also additional information, such as the index of the channel,
 * the label and the sampling frequency.
 * @author Marcin Szumski
 */
public interface ChannelSamples {

	/**
	 * Returns the number of the channel.
	 * @return the number of the channel
	 */
	int getChannelNumber();

	/**
	 * Returns the samples in this channel in the form of doubles.
	 * @return the samples in this channel in the form of doubles.
	 */
	double[] getSamples();

	/**
	 * Returns the sampling frequency.
	 * @return the number of samples per second
	 */
	float getSamplingFrequency();

	/**
	 * Returns the name of this channel.
	 * @return the name of this channel
	 */
	String getName();

}