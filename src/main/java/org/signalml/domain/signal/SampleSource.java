/* SampleSource.java created 2007-09-24
 *
 */

package org.signalml.domain.signal;

/**
 * This is an interface for an abstract source of samples.
 * Allows to return samples for the specified fragment of the signal.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SampleSource {

        /**
         * Returns if the implementation is capable of returning a calibration
         * @return true if the implementation is capable of returning a
         * calibration, false otherwise
         */
	boolean isCalibrationCapable();

         /**
         * Returns if the implementation is capable of returning a
         * sampling frequency
         * @return true if the implementation is capable of returning a
         * sampling frequency, false otherwise
         */
	boolean isSamplingFrequencyCapable();

        /**
         * Returns if the implementation is capable of returning a channel count
         * @return true if the implementation is capable of returning a channel
         * count, false otherwise
         */
	boolean isChannelCountCapable();

        /**
         * Returns the sampling frequency
         * @return the sampling frequency
         */
	float getSamplingFrequency();

        /**
         * Returns the number of samples
         * @return the number of samples
         */
	int getSampleCount();

        /**
         * Returns the calibration
         * @return the calibration
         */
	float getCalibration();

        /**
         * Returns the given number of samples starting from a given position
         * in time.
         * @param target the array to which results will be written starting
         * from position <code>arrayOffset</code>
         * @param signalOffset the position (in time) in the signal starting
         * from which samples will be returned
         * @param count the number of samples to be returned
         * @param arrayOffset the offset in <code>target</code> array starting
         * from which samples will be written
         */
	void getSamples(double[] target, int signalOffset, int count, int arrayOffset);

        /**
         * Returns the label of the channel
         * @return a string with a label of the channel
         */
	String getLabel();

}
