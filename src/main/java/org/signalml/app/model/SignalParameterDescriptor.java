/* SignalParameterDescriptor.java created 2007-09-27
 *
 */

package org.signalml.app.model;

/** SignalParameterDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalParameterDescriptor extends PagingParameterDescriptor {

	private Float samplingFrequency = 128F;
	private Integer channelCount = 1;
	private Float calibration = null;

	private boolean samplingFrequencyEditable = true;
	private boolean channelCountEditable = true;
	private boolean calibrationEditable = true;

	public Float getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency(Float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	public Integer getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(Integer channelCount) {
		this.channelCount = channelCount;
	}

	public Float getCalibration() {
		return calibration;
	}

	public void setCalibration(Float calibration) {
		this.calibration = calibration;
	}

	public boolean isSamplingFrequencyEditable() {
		return samplingFrequencyEditable;
	}

	public void setSamplingFrequencyEditable(boolean samplingFrequencyEnabled) {
		this.samplingFrequencyEditable = samplingFrequencyEnabled;
	}

	public boolean isChannelCountEditable() {
		return channelCountEditable;
	}

	public void setChannelCountEditable(boolean channelCountEnabled) {
		this.channelCountEditable = channelCountEnabled;
	}

	public boolean isCalibrationEditable() {
		return calibrationEditable;
	}

	public void setCalibrationEditable(boolean calibrationEnabled) {
		this.calibrationEditable = calibrationEnabled;
	}

}
