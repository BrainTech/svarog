/* ChannelSelectorSampleSource.java created 2007-09-24
 *
 */

package org.signalml.domain.signal;

/**
 * This class represents the {@link SampleSource source of samples} for
 * the selected channel.
 * It holds all the source for all channels, but behaves like there was only
 * one.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelSelectorSampleSource implements SampleSource {

        /**
         * the {@link MultichannelSampleSource sample source} for all channels
         */
	private MultichannelSampleSource source;

        /**
         * the number of the channel that this sample source represents
         */
        private int channel;

	/**
         * Constructor. Creates a sample source for a single channel using a
         * {@link MultichannelSampleSource sample source} for all channels and
         * the number of the channel
         * @param source the sample source for all channels
         * @param channel the number of the channel that this sample source
         * will represent
         */
        public ChannelSelectorSampleSource(MultichannelSampleSource source, int channel) {
		super();
		this.source = source;
		this.channel = channel;
	}

        /**
         * Returns the {@link MultichannelSampleSource sample source} for all
         * channels.
         * @return the sample source for all channels
         */
	public MultichannelSampleSource getSource() {
		return source;
	}

        /**
         * Returns the number of the channel that this sample source represents
         * @return the number of the channel that this sample source represents
         */
	public int getChannel() {
		return channel;
	}

	@Override
	public float getCalibration() {
		return source.getCalibration();
	}

	@Override
	public String getLabel() {
		return source.getLabel(channel);
	}

	@Override
	public int getSampleCount() {
		return source.getSampleCount(channel);
	}

	@Override
	public void getSamples(double[] target, int signalOffset, int count, int arrayOffset) {
		source.getSamples(channel, target, signalOffset, count, arrayOffset);
	}

	@Override
	public float getSamplingFrequency() {
		return source.getSamplingFrequency();
	}

        /**
         * Returns if the {@link MultichannelSampleSource sample source} for all
         * channels is capable of returning a calibration
         * @return true if the sample source for all channels is capable of
         * returning a calibration, false otherwise
         */
	@Override
	public boolean isCalibrationCapable() {
		return source.isCalibrationCapable();
	}

        /**
         * Returns if the {@link MultichannelSampleSource sample source} for all
         * channels is capable of returning a channel count
         * @return true if the sample source for all channels is capable of
         * returning a channel count, false otherwise
         */
	@Override
	public boolean isChannelCountCapable() {
		return source.isChannelCountCapable();
	}

        /**
         * Returns if the {@link MultichannelSampleSource sample source} for all
         * channels is capable of returning a sampling frequency
         * @return true if the sample source for all channels is capable of
         * returning a sampling frequency, false otherwise
         */
	@Override
	public boolean isSamplingFrequencyCapable() {
		return source.isSamplingFrequencyCapable();
	}

}
