/* SignalFragmentSampleSource.java created 2007-11-02
 *
 */

package org.signalml.domain.signal;

/** SignalFragmentSampleSource
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalFragmentSampleSource extends MultichannelSampleProcessor {

	protected int channel;
	protected int minSample;
	protected int maxSample;
	protected int length;

	protected SignalFragmentSampleSource(MultichannelSampleSource source) {
		super(source);
	}

	public SignalFragmentSampleSource(MultichannelSampleSource source, int channel, int minSample, int maxSample) {
		super(source);
		this.channel = channel;
		this.minSample = minSample;
		this.maxSample = maxSample;
		this.length = 1 + (maxSample - minSample);
	}

	public int getChannel() {
		return channel;
	}

	public int getMinSample() {
		return minSample;
	}

	public int getMaxSample() {
		return maxSample;
	}

	public int getLength() {
		return length;
	}

	@Override
	public int getChannelCount() {
		if (channel == SignalSelection.CHANNEL_NULL) {
			return super.getChannelCount();
		} else {
			return 1;
		}
	}

	@Override
	public int getSampleCount(int channel) {
		return length;
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		int realChannel = (this.channel != SignalSelection.CHANNEL_NULL ? this.channel : channel);
		source.getSamples(realChannel, target, minSample+signalOffset, count, arrayOffset);

	}

}
