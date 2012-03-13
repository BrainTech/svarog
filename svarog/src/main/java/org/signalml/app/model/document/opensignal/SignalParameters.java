package org.signalml.app.model.document.opensignal;

import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;

public class SignalParameters {
	
	private float samplingFrequency;
	private int channelCount;
	private float[] calibrationGain;
	private float[] calibrationOffset;
	
	private float pageSize;
	private int blocksPerPage;
	
	private Float maximumValue;
	private Float minimumValue;

	public SignalParameters() {
        samplingFrequency = 128.0F;
        pageSize = 20.0F;
	}

	public SignalParameters(SignalParameters signalParameters) {
		samplingFrequency = signalParameters.samplingFrequency;
		channelCount = signalParameters.channelCount;
		calibrationGain = signalParameters.calibrationGain == null ? null : signalParameters.calibrationGain.clone();
		calibrationOffset = signalParameters.calibrationOffset == null ? null : signalParameters.calibrationOffset.clone();

		pageSize = signalParameters.pageSize;
		blocksPerPage = signalParameters.blocksPerPage;

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
	public float getPageSize() {
		return pageSize;
	}
	public void setPageSize(float pageSize) {
		this.pageSize = pageSize;
	}
	public int getBlocksPerPage() {
		return blocksPerPage;
	}
	public void setBlocksPerPage(int blocksPerPage) {
		this.blocksPerPage = blocksPerPage;
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

}