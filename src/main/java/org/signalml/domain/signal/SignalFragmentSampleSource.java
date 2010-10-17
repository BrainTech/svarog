/* SignalFragmentSampleSource.java created 2007-11-02
 *
 */

package org.signalml.domain.signal;

import org.signalml.plugin.export.signal.SignalSelection;

/**
 * This class represents the {@link MultichannelSampleSource source} of samples
 * for the fragment (in time) of multichannel signal.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalFragmentSampleSource extends MultichannelSampleProcessor {

        /**
         * the index of the default channel that will be used in the case
         * of no index provided
         */
	protected int channel;

        /**
         * the index in the actual source of the first sample in this
         * fragment
         */
	protected int minSample;

        /**
         * the index in the actual source of the last sample in this
         * fragment
         */
	protected int maxSample;

        /**
         * the length (number of samples) of the fragment
         */
        protected int length;

        /**
         * Constructor. Creates the source of samples for the fragment of
         * signal, but without setting the fragment.
         * @param source the actual source of signal
         */
	protected SignalFragmentSampleSource(MultichannelSampleSource source) {
		super(source);
	}

        /**
         * Constructor. Creates the source of samples for the given fragment of
         * signal.
         * @param source the actual {@link MultichannelSampleSource source}
         * of signal
         * @param channel the index of the default channel that will be used
         * in the case of no channel index provided
         * @param minSample the index in the actual source of the first sample
         * in this fragment
         * @param maxSample the index in the actual source of the last sample
         * in this fragment
         */
	public SignalFragmentSampleSource(MultichannelSampleSource source, int channel, int minSample, int maxSample) {
		super(source);
		this.channel = channel;
		this.minSample = minSample;
		this.maxSample = maxSample;
		this.length = 1 + (maxSample - minSample);
	}

        /**
         * Returns the index of the default channel that is used
         * in the case of no channel index provided.
         * @return the index of the default channel
         */
	public int getChannel() {
		return channel;
	}

        /**
         * Returns the index in the actual source of the first sample
         * in this fragment.
         * @return the index in the actual source of the first sample
         * in this fragment
         */
	public int getMinSample() {
		return minSample;
	}

        /**
         * Returns the index in the actual source of the last sample
         * in this fragment.
         * @return the index in the actual source of the last sample
     * in this fragment
         */
	public int getMaxSample() {
		return maxSample;
	}

        /**
         * Returns the length of the fragment (the number of samples).
         * @return the length of the fragment
         */
	public int getLength() {
		return length;
	}

        /**
         * Returns the number of channels in the signal if no channel
         * (<code>SignalSelection.CHANNEL_NULL</code>) is set as the default,
         * 1 otherwise.
         * @return the number of channels in the signal if no channel
         * (<code>SignalSelection.CHANNEL_NULL</code>) is set as the default,
         * 1 otherwise
         */
	@Override
	public int getChannelCount() {
		if (channel == SignalSelection.CHANNEL_NULL) {
			return super.getChannelCount();
		} else {
			return 1;
		}
	}

        /**
         * Returns the number of samples in this fragment.
         * @param channel the index of the channel, not used
         * @return  the number of samples in this fragment
         */
	@Override
	public int getSampleCount(int channel) {
		return length;
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		int realChannel = (this.channel != SignalSelection.CHANNEL_NULL ? this.channel : channel);
		source.getSamples(realChannel, target, minSample+signalOffset, count, arrayOffset);

	}
        //TODO can return samples that are outside given fragment

}
