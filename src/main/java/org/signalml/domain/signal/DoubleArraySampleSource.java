/* DoubleArraySampleSource.java created 2008-01-15
 *
 */

package org.signalml.domain.signal;

/**
 * This class represents a {@link MultichannelSampleSource source} for
 * a multichannel signal. It is based on an 2-dimensional array of samples
 * (first dimension - number of channel, second - time).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DoubleArraySampleSource extends AbstractMultichannelSampleSource implements MultichannelSampleSource {

	protected double[][] samples;
	protected int channelCount;
	protected int sampleCount;

        /**
         * Constructor. Creates a <code>DoubleArraySampleSource</code> with a
         * given number of channels, a given number of samples and a given array
         * of samples.
         * @param samples the 2D array with samples
         * @param channelCount the number of channels
         * @param sampleCount the number of samples per channel
         */
	public DoubleArraySampleSource(double[][] samples, int channelCount, int sampleCount) {
		super();
		this.samples = samples;
		this.channelCount = channelCount;
		this.sampleCount = sampleCount;
	}

	@Override
	public int getChannelCount() {
		return channelCount;
	}

        /**
         * Returns the number of the channel. The same value as given
         * @param channel the number of a channel
         * @return the number of the channel, the same value as given
         */
	@Override
	public int getDocumentChannelIndex(int channel) {
		return channel;
	}

	@Override
	public String getLabel(int channel) {
		return "L" + (channel+1);
	}

	@Override
	public int getSampleCount(int channel) {
		return sampleCount;
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		for (int i=0; i<count; i++) {
			target[arrayOffset+i] = samples[channel][signalOffset+i];
		}
	}
        //TODO shouldn't we check if index is not out of bound?

        /**
         * Returns the number of samples per second
         * @return the number of samples per second = 128
         */
	@Override
	public float getSamplingFrequency() {
		return 128F;
	}

        /**
         * Returns if the implementation is capable of returning a channel count
         * @return the implementation is capable of returning a channel
         * count, so true
         */
	@Override
	public boolean isChannelCountCapable() {
		return true;
	}

        /**
         * Returns if the implementation is capable of returning a
         * sampling frequency
         * @return true the implementation is not capable of returning a
         * sampling frequency, so false
         */
	@Override
	public boolean isSamplingFrequencyCapable() {
		return false;
	}

	@Override
	public void destroy() {
		// do nothing
	}

}
