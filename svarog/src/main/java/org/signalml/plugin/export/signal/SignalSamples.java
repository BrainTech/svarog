package org.signalml.plugin.export.signal;


/**
 * This interface allows to return {@link ChannelSamples samples} for a selected channel
 * and for all channels.
 * @author Marcin Szumski
 */
public interface SignalSamples {

	/**
	 * Returns signal samples for a given channel.
	 * @param channelNumber the index of the channel
	 * @return signal samples for a given channel
	 */
	ChannelSamples getChannelSamples(int channelNumber);

	/**
	 * Returns arrayList of {@link ChannelSamples channel samples} for all channels.
	 * @return arrayList of channel samples for all channels
	 */
	ChannelSamples[] getChannels();

}