/* ResamplableSampleSource.java created 2008-01-30
 * 
 */

package org.signalml.domain.signal;

/** ResamplableSampleSource
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface ResamplableSampleSource {

	void getRawSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset);
	
}
