package org.signalml.app.model.document.opensignal;

import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;

public class SignalParameters {

	private RawSignalSampleType sampleType;
	private RawSignalByteOrder byteOrder;
	
	private float samplingFrequency;
	private int channelCount;
	private float[] calibrationGain;
	private float[] calibrationOffset;
	
	private float pageSize;
	private int blocksPerPage;
	
	private Float maximumValue;
	private Float minimumValue;

	public SignalParameters() {
		sampleType = RawSignalSampleType.DOUBLE;
        byteOrder = RawSignalByteOrder.LITTLE_ENDIAN;
        samplingFrequency = 128.0F;
        pageSize = 20.0F;
	}
	
	public RawSignalSampleType getSampleType() {
		return sampleType;
	}
	public void setSampleType(RawSignalSampleType sampleType) {
		this.sampleType = sampleType;
	}
	public RawSignalByteOrder getByteOrder() {
		return byteOrder;
	}
	public void setByteOrder(RawSignalByteOrder byteOrder) {
		this.byteOrder = byteOrder;
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