/* DoubleArraySampleSource.java created 2008-01-15
 * 
 */

package org.signalml.domain.signal;

/** DoubleArraySampleSource
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DoubleArraySampleSource extends AbstractMultichannelSampleSource implements MultichannelSampleSource {

	private double[][] samples;
	private int channelCount;
	private int sampleCount;

	public DoubleArraySampleSource(double[][] samples, int channelCount, int sampleCount) {
		super();
		this.samples = samples;
		this.channelCount = channelCount;
		this.sampleCount = sampleCount;		
	}

	@Override
	public float getCalibration() {
		return 1F;
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
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		for( int i=0; i<count; i++ ) {
			target[arrayOffset+i] = samples[channel][signalOffset+i];
		}
	}

	@Override
	public float getSamplingFrequency() {
		return 128F;
	}

	@Override
	public boolean isCalibrationCapable() {
		return false;
	}

	@Override
	public boolean isChannelCountCapable() {
		return true;
	}

	@Override
	public boolean isSamplingFrequencyCapable() {
		return false;
	}
	
	@Override
	public void destroy() {
		// do nothing
	}
	
}
