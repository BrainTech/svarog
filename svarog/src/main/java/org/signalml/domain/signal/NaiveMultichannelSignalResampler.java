/* NaiveMultichannelSignalUpsampler.java created 2008-01-30
 *
 */

package org.signalml.domain.signal;

import org.signalml.domain.signal.samplesource.ResamplableSampleSource;

/**
 * This class is an implementation of the naive resampler of the signal.
 * Samples are not interpolated but the nearest (to this point of time) sample
 * is taken.
 *
 * @see MultichannelSignalResampler
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NaiveMultichannelSignalResampler implements MultichannelSignalResampler {

	/**
	 * the buffer of signal samples
	 */
	private double[] tempBuffer = null;

	/**
	 * For a given channel creates an array of samples that are between two
	 * points in time:
	 * 1. <code>externalSignalOffset / externalFrequency</code>
	 * 2. <code>(externalSignalOffset+externalCount)/externalFrequency<code>
	 * To calculate the indexes of these samples uses
	 * <code>internalFrequency</code>
	 * @param sampleSource the source of samples
	 * @param channel the number of channel for which samples are returned
	 * @param target an array in which result is stored
	 * @param externalSignalOffset the position in the RESAMPLED array from
	 * which copying starts
	 * @param externalCount the number of samples in the RESAMPLED array
	 * that are to be copied
	 * @param arrayOffset the index in <code>target</code> array writing
	 * result will start
	 * @param externalFrequency the targeted frequency
	 * @param internalFrequency the current frequency
	 */
	@Override
	public void resample(ResamplableSampleSource sampleSource, int channel, double[] target, int externalSignalOffset, int externalCount, int arrayOffset, float externalFrequency, float internalFrequency) {

		float factor = internalFrequency / externalFrequency;

		int minInternalSample = (int) Math.floor(externalSignalOffset * factor);
		int maxInternalSample = (int) Math.ceil((externalSignalOffset + externalCount -1) * factor);

		int internalCount = 1 + maxInternalSample - minInternalSample;
		if (tempBuffer == null || tempBuffer.length < internalCount) {
			tempBuffer = new double[internalCount];
		}

		sampleSource.getRawSamples(channel, tempBuffer, minInternalSample, internalCount, 0);

		int i;
		for (i=0; i<externalCount; i++) {
			target[arrayOffset+i] = tempBuffer[ Math.round(factor * (externalSignalOffset+i)) - minInternalSample ];
		}

	}

}
