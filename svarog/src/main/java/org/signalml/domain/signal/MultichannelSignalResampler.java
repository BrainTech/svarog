/* MultichannelSignalUpsampler.java created 2008-01-30
 *
 */

package org.signalml.domain.signal;

import org.signalml.domain.signal.samplesource.ResamplableSampleSource;

/**
 * This interface represents the resampler of the signal.
 * Allows to create an array of samples that would be between given indexes
 * in an array for a signal of a different frequency.
 * To do that in some (specified by implementation) way interpolates the values
 * of these samples.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MultichannelSignalResampler {

	/**
	 * For a given channel creates an array of samples that would be
	 * between indexes
	 * <code>externalSignalOffset</code> and
	 * <code>(externalSignalOffset+externalCount)<code> in the resampled
	 * array.
	 * To calculate the current indexes of these samples uses
	 * <code>internalFrequency</code>.
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
	void resample(ResamplableSampleSource sampleSource, int channel, double[] target, int externalSignalOffset, int externalCount, int arrayOffset, float externalFrequency, float internalFrequency);

}
