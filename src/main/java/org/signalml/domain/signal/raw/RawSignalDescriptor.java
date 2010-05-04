/* RawSignalDescriptor.java created 2008-01-18
 * 
 */

package org.signalml.domain.signal.raw;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** RawSignalDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("rawdescriptor")
public class RawSignalDescriptor {

	public enum SourceSignalType { 
		
		RAW,
		SIGNALML
		
	};
	
	private String exportFileName;
	
	private String sourceFileName;
	private SourceSignalType sourceSignalType;
	private String sourceSignalMLFormat;
	private String sourceSignalMLSourceUID;
	
	private float samplingFrequency;
	private int channelCount;
	private int sampleCount;
	private float calibration;

    private float[] calibrationGain;
    private float[] calibrationOffset;
    private Float minimumValue;
    private Float maximumValue;

	private RawSignalSampleType sampleType;
	private RawSignalByteOrder byteOrder;
	
	private float pageSize;
	private int blocksPerPage;
	
	private String[] channelLabels;
	
	private double markerOffset;
	private Date exportDate;
	
	public RawSignalDescriptor() {
	}
	
	public String getExportFileName() {
		return exportFileName;
	}
	
	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}
	
	public String getSourceFileName() {
		return sourceFileName;
	}
	
	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}
	
	public SourceSignalType getSourceSignalType() {
		return sourceSignalType;
	}
	
	public void setSourceSignalType(SourceSignalType sourceSignalType) {
		this.sourceSignalType = sourceSignalType;
	}
	
	public String getSourceSignalMLFormat() {
		return sourceSignalMLFormat;
	}
	
	public void setSourceSignalMLFormat(String sourceSignalMLFormat) {
		this.sourceSignalMLFormat = sourceSignalMLFormat;
	}
	
	public String getSourceSignalMLSourceUID() {
		return sourceSignalMLSourceUID;
	}
	
	public void setSourceSignalMLSourceUID(String sourceSignalMLSourceUID) {
		this.sourceSignalMLSourceUID = sourceSignalMLSourceUID;
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
	
	public int getSampleCount() {
		return sampleCount;
	}
	
	public void setSampleCount(int sampleCount) {
		this.sampleCount = sampleCount;
	}
	
	public float getCalibration() {
		return calibration;
	}
	
	public void setCalibration(float calibration) {
		this.calibration = calibration;
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

    public Float getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(Float minimumValue) {
        this.minimumValue = minimumValue;
    }

    public Float getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(Float maximumValue) {
        this.maximumValue = maximumValue;
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
	
	public String[] getChannelLabels() {
		return channelLabels;
	}
	
	public void setChannelLabels(String[] channelLabels) {
		this.channelLabels = channelLabels;
	}
	
	public double getMarkerOffset() {
		return markerOffset;
	}
	
	public void setMarkerOffset(double markerOffset) {
		this.markerOffset = markerOffset;
	}
	
	public Date getExportDate() {
		return exportDate;
	}
	
	public void setExportDate(Date exportDate) {
		this.exportDate = exportDate;
	}
	
}
