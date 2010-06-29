/* MultichannelSampleSource.java created 2007-09-24
 *
 */

package org.signalml.domain.signal;

import java.beans.PropertyChangeListener;

/** MultichannelSampleSource
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MultichannelSampleSource {

	public static final String SAMPLING_FREQUENCY_PROPERTY = "samplingFrequency";
	public static final String CALIBRATION_PROPERTY = "calibration";
	public static final String CHANNEL_COUNT_PROPERTY = "channelCount";
	public static final String LABEL_PROPERTY = "channelCount";

	boolean isSamplingFrequencyCapable();
	boolean isChannelCountCapable();
	boolean isCalibrationCapable();

	float getSamplingFrequency();

	int getChannelCount();
	float getCalibration();

	int getSampleCount(int channel);

	void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset);

	String getLabel(int channel);

	int getDocumentChannelIndex(int channel);

	public void addPropertyChangeListener(PropertyChangeListener listener);
	public void removePropertyChangeListener(PropertyChangeListener listener);

	public void destroy();

}
