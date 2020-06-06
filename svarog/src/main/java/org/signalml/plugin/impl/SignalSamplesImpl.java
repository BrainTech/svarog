/**
 *
 */
package org.signalml.plugin.impl;

import java.util.ArrayList;
import org.signalml.plugin.export.signal.ChannelSamples;
import org.signalml.plugin.export.signal.SignalSamples;

/**
 * This class holds samples for all channels of the signal.
 * Contains the list of {@link ChannelSamplesImpl samples for each channel}
 * @author Marcin Szumski
 */
public class SignalSamplesImpl implements SignalSamples {

	/**
	 * the ArrayList of {@link ChannelSamplesImpl channel samples} for each channel
	 */
	private ArrayList<ChannelSamplesImpl> channels = new ArrayList<ChannelSamplesImpl>();

	/**
	 * Empty constructor.
	 */
	public SignalSamplesImpl() {
	}

	/**
	 * Constructor.
	 * @param channels ArrayList of channel samples for each channel
	 */
	public SignalSamplesImpl(ChannelSamplesImpl[] channels) {
		for (ChannelSamplesImpl samples : channels) {
			this.channels.add(samples);
		}
	}

	/**
	 * Adds samples for one channel.
	 * @param channelSamples samples for one channel
	 */
	public void addChannelSamples(ChannelSamplesImpl channelSamples) {
		channels.add(channelSamples.getChannelNumber(), channelSamples);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.SignalSamples#getChannelSamples(int)
	 */
	@Override
	public ChannelSamples getChannelSamples(int channelNumber) {
		return channels.get(channelNumber);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.SignalSamples#getChannels()
	 */
	@Override
	public ChannelSamples[] getChannels() {
		return channels.toArray(new ChannelSamplesImpl[channels.size()]);
	}

}
