/* ChannelSubsetSampleSource.java created 2008-01-30
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.MultichannelSampleSource;

/** ChannelSubsetSampleSource
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelSubsetSampleSource extends MultichannelSampleProcessor {

	private int channelCount;
	private int[] channelIndices;

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

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		source.getSamples(channelIndices[channel], target, signalOffset, count, arrayOffset);
	}

	@Override
	public int getDocumentChannelIndex(int channel) {
		return source.getDocumentChannelIndex(channelIndices[channel]);
	}

	@Override
	public int getChannelCount() {
		return channelCount;
	}

	@Override
	public int getSampleCount(int channel) {
		return source.getSampleCount(channel);
	}

	@Override
	public String getLabel(int channel) {
		return source.getLabel(channelIndices[channel]);
	}

}
