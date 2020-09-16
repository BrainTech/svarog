/* MultichannelSampleSource.java created 2007-09-24
 *
 */

package org.signalml.domain.signal.samplesource;

import java.beans.PropertyChangeListener;

/**
 * This is an interface representing source of samples for a multichannel signal.
 * Most important is a function returning samples for a given channel.
 * Additional functions allow to return number of channels, number of samples,
 * sampling frequency, calibration and labels for channels.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MultichannelSampleSource {

	public static final String SAMPLING_FREQUENCY_PROPERTY = "samplingFrequency";
	public static final String CALIBRATION_PROPERTY = "calibration";
	public static final String CHANNEL_COUNT_PROPERTY = "channelCount";
	public static final String LABEL_PROPERTY = "channelCount";

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
	 * Returns the number of samples per second
	 * @return the number of samples per second
	 */
	float getSamplingFrequency();

	/**
	 * Returns the number of channels in this source
	 * @return the number of channels in this source
	 */
	int getChannelCount();

	/**
	 * Returns the number of samples for a given channel
	 * @param channel the index of the channel
	 * @return the number of samples for a given channel
	 */
	int getSampleCount(int channel);

	/**
	 * Returns the given number of samples for a given channel starting
	 * from a given position in time.
	 * @param channel the number of channel
	 * @param target the array to which results will be written starting
	 * from position <code>arrayOffset</code>
	 * @param signalOffset the position (in time) in the signal starting
	 * from which samples will be returned
	 * @param count the number of samples to be returned
	 * @param arrayOffset the offset in <code>target</code> array starting
	 * from which samples will be written
	 * @return for on-line signals, total number of received samples; 0 otherwise
	 */
	long getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset);

	/**
	 * Returns the label of a channel of a given index
	 * @param channel the index of a channel
	 * @return a string with a label of the channel
	 */
	String getLabel(int channel);

	/**
	 * Returns the index of a given channel in the document
	 * @param channel the index of a channel
	 * @return the index of a given channel in the document
	 */
	int getDocumentChannelIndex(int channel);

	/**
	 * Adds the given listener to the collection of listeners associated
	 * with this sample source.
	 * @param listener the listener to be added
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Removes the given listener from the collection of listeners associated
	 * with this sample source.
	 * @param listener the listener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Destroys this sample source. If necessary performs additional actions.
	 */
	public void destroy();

}
