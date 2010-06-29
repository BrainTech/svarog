/* SampleSource.java created 2007-09-24
 *
 */

package org.signalml.domain.signal;

/** SampleSource
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SampleSource {

	boolean isCalibrationCapable();
	boolean isSamplingFrequencyCapable();
	boolean isChannelCountCapable();

	float getSamplingFrequency();

	int getSampleCount();

	float getCalibration();

	void getSamples(double[] target, int signalOffset, int count, int arrayOffset);

	String getLabel();

}
