/* OriginalMultichannelSampleSource.java created 2007-09-28
 *
 */

package org.signalml.domain.signal;

import org.signalml.exception.SignalMLException;

/**
 * This interface represents the source of samples for the multichannel signal
 * that is stored in the file.
 * The form of storage depends on the implementation.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface OriginalMultichannelSampleSource extends MultichannelSampleSource {

        /**
         * Sets the sampling frequency (number of samples per second) to a
         * given value
         * @param samplingFrequency sampling frequency (number of samples per
         * second) to be set
         */
	void setSamplingFrequency(float samplingFrequency);

        /**
         * Sets the number of signal channels.
         * @param channelCount the number of signal channels to be set
         */
        void setChannelCount(int channelCount);

        /**
         * Sets the new value of calibration.
         * @param calibration the new value of calibration
         */
	void setCalibration(float calibration);

        /**
         * Creates the copy of this sample source.
         * @return the copy of this sample source.
         * @throws SignalMLException depends on the implementation
         */
	OriginalMultichannelSampleSource duplicate() throws SignalMLException;

}
