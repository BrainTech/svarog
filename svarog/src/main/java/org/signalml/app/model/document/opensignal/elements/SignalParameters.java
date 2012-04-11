package org.signalml.app.model.document.opensignal.elements;

import org.signalml.app.model.signal.PagingParameterDescriptor;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;

public class SignalParameters extends PagingParameterDescriptor {
	
	private float samplingFrequency;
	private int channelCount;
	private float[] calibrationGain;
	private float[] calibrationOffset;
	
	private Float maximumValue;
	private Float minimumValue;
	
	private boolean channelCountEditable = false;
	private boolean calibrationEditable = true;

	public SignalParameters() {
        samplingFrequency = 128.0F;
        channelCount = 1;
	}

	public SignalParameters(SignalParameters signalParameters) {
		samplingFrequency = signalParameters.samplingFrequency;
		channelCount = signalParameters.channelCount;
		calibrationGain = signalParameters.calibrationGain == null ? null : signalParameters.calibrationGain.clone();
		calibrationOffset = signalParameters.calibrationOffset == null ? null : signalParameters.calibrationOffset.clone();

		setPageSize(signalParameters.getPageSize());
		setBlocksPerPage(signalParameters.getBlocksPerPage());

		maximumValue = signalParameters.maximumValue;
		minimumValue = signalParameters.minimumValue;
	}
	public float getSamplingFrequency() {
		return samplingFrequency;
	}
	public void setSamplingFrequency(float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}
	public int getChannelCount() {
		return channelCount;
	}
	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}
	public float[] getCalibrationGain() {
		return calibrationGain;
	}
	public void setCalibrationGain(float[] calibrationGain) {
		this.calibrationGain = calibrationGain;
	}
	public float[] getCalibrationOffset() {
		return calibrationOffset;
	}
	public void setCalibrationOffset(float[] calibrationOffset) {
		this.calibrationOffset = calibrationOffset;
	}
	public Float getMaximumValue() {
		return maximumValue;
	}
	public void setMaximumValue(Float maximumValue) {
		this.maximumValue = maximumValue;
	}
	public Float getMinimumValue() {
		return minimumValue;
	}
	public void setMinimumValue(Float minimumValue) {
		this.minimumValue = minimumValue;
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