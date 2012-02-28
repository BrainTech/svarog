/* SignalParameterDescriptor.java created 2007-09-27
 *
 */

package org.signalml.app.model.signal;


/** SignalParameterDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalParameterDescriptor extends PagingParameterDescriptor {

	private Float samplingFrequency = 128F;
	private Integer channelCount = 1;

	/**
	 * Calibration gain for the signal (the number by which each sample
	 * in the signal should be multiplied).
	 */
	private Float calibrationGain = null;

	/**
	 * Calibration offset for the signal (the number which should be
	 * added to each sample in the signal).
	 */
	private Float calibrationOffset = null;

	private boolean channelCountEditable = false;
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

	/**
	 * Returns the calibration gain for the signal.
	 * @return the calibration gain for the signal
	 */
	public Float getCalibrationGain() {
		return calibrationGain;
	}

	/**
	 * Sets a new value of calibration gain for the signal.
	 * @param calibration new value of calibration gain
	 */
	public void setCalibrationGain(Float calibration) {
		this.calibrationGain = calibration;
	}

	/**
	 * Returns the calibration offset for the signal.
	 * @return the calibration offset for the signal
	 */
	public Float getCalibrationOffset() {
		return calibrationOffset;
	}

	/**
	 * Sets the calibration offset for the signal.
	 * @param calibrationOffset new value of calibration offset
	 */
	public void setCalibrationOffset(Float calibrationOffset) {
		this.calibrationOffset = calibrationOffset;
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
