/* SampleSource.java created 2007-09-24
 *
 */

package org.signalml.domain.signal.samplesource;

/**
 * This is an interface for an abstract source of samples.
 * Allows to return samples for the specified fragment of the signal.
 * Additionally returns, if possible, the number of samples, calibration
 * and sampling frequency.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SampleSource {

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
	 * Returns the given number of samples starting from a given position
	 * in time.
	 * @param target the array to which results will be written starting
	 * from position <code>arrayOffset</code>
	 * @param signalOffset the position (in time) in the signal starting
	 * from which samples will be returned
	 * @param count the number of samples to be returned
	 * @param arrayOffset the offset in <code>target</code> array starting
	 * from which samples will be written
	 * @return total number of samples preceding the first currently available sample
	 * (one that would be accessed with signalOffset=0),
	 * this may be non-zero only in case of on-line signals
	 */
	long getSamples(double[] target, int signalOffset, int count, int arrayOffset);

	/**
	 * Returns the label of the channel
	 * @return a string with a label of the channel
	 */
	String getLabel();

}
