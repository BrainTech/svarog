/* TestMultichannelSampleSource.java created 2007-09-24
 *
 */

package org.signalml.domain.signal.test;

import java.beans.PropertyChangeListener;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

/** TestMultichannelSampleSource
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MultichannelSampleSourceMock implements MultichannelSampleSource {

	private int channelCount;
	private int sampleCount;

	public MultichannelSampleSourceMock(int channelCount, int sampleCount) {
		super();
		this.channelCount = channelCount;
		this.sampleCount = sampleCount;
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		for (int i=0; i<count; i++) {
			target[arrayOffset+i] = Math.sin(2*Math.PI*((double) signalOffset+i)/128 + 2*Math.PI*channel/((double) channelCount));
		}
	}

	@Override
	public int getChannelCount() {
		return channelCount;
	}

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
	public float getSamplingFrequency() {
		return 128.0F;
	}

	@Override
	public boolean isChannelCountCapable() {
		return true;
	}

	@Override
	public boolean isSamplingFrequencyCapable() {
		return true;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	}

	@Override
	public void destroy() {
		// do nothing
	}

}
