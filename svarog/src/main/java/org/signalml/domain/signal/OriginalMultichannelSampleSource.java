/* OriginalMultichannelSampleSource.java created 2007-09-28
 *
 */

package org.signalml.domain.signal;

import org.signalml.plugin.export.SignalMLException;

/**
 * This interface represents the source of samples for the multichannel signal
 * that is stored in the file.
 * The form of storage depends on the implementation.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface OriginalMultichannelSampleSource extends MultichannelSampleSource {

	/**
         * Returns if the implementation is capable of returning a calibration
         * @return true if the implementation is capable of returning a
         * calibration, false otherwise
         */
	boolean isCalibrationCapable();

	/**
	 * Returns if the implementation is capable of returning and setting
	 * a calibration for each channel. It can be used to determine, if
	 * - for example - method {@link OriginalMultichannelSampleSource#setCalibrationGain(float[])}
	 * can be called for the implementation.
	 *
	 * @return true, if calibration could be get or set for each channel,
	 * false if implementation is not calibration capable
	 * ({@link OriginalMultichannelSampleSource#isCalibrationCapable() }
	 * or the implementation is capable of getting or setting calibration
	 * only for all channels
	 */
	boolean areIndividualChannelsCalibrationCapable();

	/**
	 * Returns a single value representing the calibration gain.
	 * If the implementation enables calibrating each channel, then
	 * calibration gain for the first channel is returned.
	 * @return the calibration gain
	 */
	float getSingleCalibrationGain();

	/**
         * Returns the calibration gain for each channel.
         * @return an array containing calibration gain for each channel
         */
	float[] getCalibrationGain();

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
         * Sets the new value of calibration. Could be used only if the source
	 * is capable of calibrating each channel
	 * (see: {@link OriginalMultichannelSampleSource#areIndividualChannelsCalibrationCapable()}.
	 *
         * @param calibration the new value of calibration
         */
	void setCalibrationGain(float[] calibration);

	/**
	 * Sets an identical value of calibration gain for all channels.
	 * @param calibration new calibration value
	 */
	void setCalibrationGain(float calibration);

	/**
	 * Returns the values of calibration offset for the sample source for
	 * each channel.
	 * @return the values of calibration offset for the signal
	 */
	float[] getCalibrationOffset();

	/**
	 * Returns a single value representing calibration offset.
	 * @return single value representing calibration offset
	 */
	float getSingleCalibrationOffset();

	/**
	 * Sets the values of calibration offset for each channel in the sample source.
	 * @param calibrationOffset the new values of calibration offset
	 */
	void setCalibrationOffset(float[] calibrationOffset);

	/**
	 * Sets a new value of calibration offset (each channel will have the
	 * same value).
	 * @param calibrationOffset new value of calibration offset.
	 */
	void setCalibrationOffset(float calibrationOffset);

        /**
         * Creates the copy of this sample source.
         * @return the copy of this sample source.
         * @throws SignalMLException depends on the implementation
         */
	OriginalMultichannelSampleSource duplicate() throws SignalMLException;

}
