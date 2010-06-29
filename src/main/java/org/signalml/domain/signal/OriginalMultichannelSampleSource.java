/* OriginalMultichannelSampleSource.java created 2007-09-28
 *
 */

package org.signalml.domain.signal;

import org.signalml.exception.SignalMLException;

/** OriginalMultichannelSampleSource
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface OriginalMultichannelSampleSource extends MultichannelSampleSource {

	void setSamplingFrequency(float samplingFrequency);
	void setChannelCount(int channelCount);
	void setCalibration(float calibration);

	OriginalMultichannelSampleSource duplicate() throws SignalMLException;

}
