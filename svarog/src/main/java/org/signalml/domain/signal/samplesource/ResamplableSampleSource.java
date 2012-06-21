/* ResamplableSampleSource.java created 2008-01-30
 *
 */

package org.signalml.domain.signal.samplesource;

import org.signalml.domain.signal.MultichannelSignalResampler;

/**
 * This interface represents the source of samples that can be
 * {@link MultichannelSignalResampler resampled}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface ResamplableSampleSource {

	/**
	 * Returns the given number of raw samples for a given channel starting
	 * from a given position in time.
	 * @param channel the number of channel
	 * @param target the array to which results will be written starting
	 * from position <code>arrayOffset</code>
	 * @param signalOffset the position (in time) in the signal starting
	 * from which samples will be returned
	 * @param count the number of samples to be returned
	 * @param arrayOffset the offset in <code>target</code> array starting
	 * from which samples will be written
	 */
	void getRawSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset);

}
