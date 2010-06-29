/* MultichannelSignalUpsampler.java created 2008-01-30
 *
 */

package org.signalml.domain.signal;

/** MultichannelSignalUpsampler
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MultichannelSignalResampler {

	void resample(ResamplableSampleSource sampleSource, int channel, double[] target, int externalSignalOffset, int externalCount, int arrayOffset, float externalFrequency, float internalFrequency);

}
