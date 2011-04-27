/* ChannelSubsetSampleSource.java created 2008-01-30
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.MultichannelSampleSource;

/**
 * This class represents the source of samples for the selected channels.
 * Contains the source for all channels and the set of indexes of channels that
 * are allowed.
 * Maps the indexes of channels from stored array to indexes in the source.
 * @see #getSamples(int, double[], int, int, int)
 *
 * @see MultichannelSampleSource
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelSubsetSampleSource extends MultichannelSampleProcessor {

        /**
         * the number of channels in this source (number of selected channels)
         */
	private int channelCount;
        /**
         * the collection of indexes of selected channels.
         */
	private int[] channelIndices;

        /**
         * Constructor. Creates the {@link MultichannelSampleSource source} of
         * samples for the given {@link ChannelSpace set of channels}.
         * @param source the source of samples for all channels
         * @param channelSpace the set of channels
         */
	public ChannelSubsetSampleSource(MultichannelSampleSource source, ChannelSpace channelSpace) {
		super(source);

		// determine channel count & indices
		if (channelSpace != null) {

			channelCount = channelSpace.size();
			channelIndices = channelSpace.getSelectedChannels();

		} else { // all channels

			channelCount = source.getChannelCount();
			channelIndices = new int[channelCount];
			for (int i=0; i<channelCount; i++) {
				channelIndices[i] = i;
			}

		}

	}

        /**
         * Returns the given number of samples for a given channel starting
         * from a given position in time.
         * The index of the channel means index in the mapping array, for example:
         * {@code getSamples(i,...)} return samples for channel
         * {@code channelIndicies[i]}.
         * @see #channelIndices
         * @param channel the index of a channel in this source
         * (<code>channelIndices</code> array)
         * @param target the array to which results will be written starting
         * from position <code>arrayOffset</code>
         * @param signalOffset the position (in time) in the signal starting
         * from which samples will be returned
         * @param count the number of samples to be returned
         * @param arrayOffset the offset in <code>target</code> array starting
         * from which samples will be written
         */
	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		source.getSamples(channelIndices[channel], target, signalOffset, count, arrayOffset);
	}

        /**
         * Returns the index of a given channel in the document
         * @param channel the index of a channel in this source
         * (<code>channelIndices</code> array)
         * @return the index of a given channel in the document
         */
	@Override
	public int getDocumentChannelIndex(int channel) {
		return source.getDocumentChannelIndex(channelIndices[channel]);
	}

	@Override
	public int getChannelCount() {
		return channelCount;
	}

        /**
         * Returns the number of samples for a given channel
         * @param channel the index of a channel in this source
         * (<code>channelIndices</code> array)
         * @return the number of samples for a given channel
         */
	@Override
	public int getSampleCount(int channel) {
		return source.getSampleCount(channel);
	}

        /**
         * Returns the label of a channel of a given index
         * @param channel the index of a channel in this source
         * (<code>channelIndices</code> array)
         * @return a string with a label of the channel
         */
	@Override
	public String getLabel(int channel) {
		return source.getLabel(channelIndices[channel]);
	}

}
