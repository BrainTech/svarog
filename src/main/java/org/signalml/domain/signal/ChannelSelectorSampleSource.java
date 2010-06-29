/* ChannelSelectorSampleSource.java created 2007-09-24
 *
 */

package org.signalml.domain.signal;

/** ChannelSelectorSampleSource
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelSelectorSampleSource implements SampleSource {

	private MultichannelSampleSource source;
	private int channel;

	public ChannelSelectorSampleSource(MultichannelSampleSource source, int channel) {
		super();
		this.source = source;
		this.channel = channel;
	}

	public MultichannelSampleSource getSource() {
		return source;
	}

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

	@Override
	public boolean isCalibrationCapable() {
		return source.isCalibrationCapable();
	}

	@Override
	public boolean isChannelCountCapable() {
		return source.isChannelCountCapable();
	}

	@Override
	public boolean isSamplingFrequencyCapable() {
		return source.isSamplingFrequencyCapable();
	}

}
