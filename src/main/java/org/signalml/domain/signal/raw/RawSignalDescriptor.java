/* RawSignalDescriptor.java created 2008-01-18
 *
 */

package org.signalml.domain.signal.raw;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.Arrays;

/**
 * This class represents the descriptor of a raw signal.
 * I consists of basic parameters of the signal, such as the number of channels,
 * the frequency of sampling, length, associated files (source and destination),
 * {@link SourceSignalType source type}, {@link RawSignalSampleType sample type}
 * and {@link RawSignalByteOrder byte order}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("rawdescriptor")
public class RawSignalDescriptor {

        /**
         * This enumerator tells if the signal is stored as raw or described
         * by signalML codec.
         */
	public enum SourceSignalType {

                /**
                 * the raw signal
                 */
		RAW,
                /**
                 * the signal described by signalML codec
                 */
		SIGNALML

	};

        /**
         * the name of the file to which the signal is exported
         */
	private String exportFileName;

        /**
         * the name of the original file with the signal
         */
	private String sourceFileName;
        /**
         * the {@link SourceSignalType type} of a source signal,
         * possible types: RAW, SIGNALML
         */
	private SourceSignalType sourceSignalType;
        /**
         * string describing the format of the singalML source
         */
	private String sourceSignalMLFormat;
        /**
         * the identifier of a signalML source
         */
	private String sourceSignalMLSourceUID;

        /**
         * the number of samples per second
         */
	private float samplingFrequency;
        /**
         * the number of channels in the signal
         */
	private int channelCount;
        /**
         * the number of samples in a single channel
         */
	private int sampleCount;

	/**
	 * The calibration gain for the signal - the value by which each sample
	 * value is multiplied.
	 */
	private float[] calibrationGain;

	/**
	 * The calibration offset for the signal - the value which is added
	 * to each sample value.
	 */
	private float[] calibrationOffset;
	private Float minimumValue;
	private Float maximumValue;

        /**
         * the {@link RawSignalSampleType type} of the samples
         */
	private RawSignalSampleType sampleType;
        /**
         * the {@link RawSignalByteOrder order} of bytes in the file with
         * the signal
         */
	private RawSignalByteOrder byteOrder;

        /**
         * the length of the page in seconds
         */
	private float pageSize;
        /**
         * the number of blocks in one page
         */
	private int blocksPerPage;

        /**
         * an array of labels of signal channels
         */
	private String[] channelLabels;

        /**
         * the position (in seconds) of the marker in the described
         * signal
         */
	private double markerOffset;
        /**
         * the time of exporting the signal to the file
         */
	private Date exportDate;

        /**
         * Constructor. Creates an empty descriptor of a raw signal.
         */
	public RawSignalDescriptor() {
	}

        /**
         * Returns the name of the file to which the signal is exported.
         * @return the name of the file to which the signal is exported
         */
	public String getExportFileName() {
		return exportFileName;
	}

        /**
         * Sets the name of the file to which the signal is exported.
         * @param exportFileName the name of the file to which the signal
         * is exported
         */
	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}

        /**
         * Returns the name of the original file with the signal.
         * @return the name of the original file with the signal
         */
	public String getSourceFileName() {
		return sourceFileName;
	}

        /**
         * Sets the name of the original file with the signal
         * @param sourceFileName the name of the original file with the signal
         */
	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

        /**
         * Returns the {@link SourceSignalType type} of a source signal
         * @return the type of a source signal
         */
	public SourceSignalType getSourceSignalType() {
		return sourceSignalType;
	}

        /**
         * Sets the {@link SourceSignalType type} of a source signal
         * @param sourceSignalType  the type of a source signal
         */
	public void setSourceSignalType(SourceSignalType sourceSignalType) {
		this.sourceSignalType = sourceSignalType;
	}

        /**
         * Returns the string describing the format of the singalML source
         * @return the string describing the format of the singalML source
         */
	public String getSourceSignalMLFormat() {
		return sourceSignalMLFormat;
	}

        /**
         * Sets the string describing the format of the singalML source
         * @param sourceSignalMLFormat the string describing the format of
         * the singalML source
         */
	public void setSourceSignalMLFormat(String sourceSignalMLFormat) {
		this.sourceSignalMLFormat = sourceSignalMLFormat;
	}

        /**
         * Returns the identifier of a signalML source
         * @return the identifier of a signalML source
         */
	public String getSourceSignalMLSourceUID() {
		return sourceSignalMLSourceUID;
	}

        /**
         * Sets the identifier of a signalML source
         * @param sourceSignalMLSourceUID the identifier of a signalML source
         */
	public void setSourceSignalMLSourceUID(String sourceSignalMLSourceUID) {
		this.sourceSignalMLSourceUID = sourceSignalMLSourceUID;
	}

        /**
         * Returns the number of samples per second
         * @return the number of samples per second
         */
	public float getSamplingFrequency() {
		return samplingFrequency;
	}

        /**
         * Sets the number of samples per second
         * @param samplingFrequency the number of samples per second
         */
	public void setSamplingFrequency(float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

        /**
         * Returns the number of channels in the signal
         * @return the number of channels in the signal
         */
	public int getChannelCount() {
		return channelCount;
	}

        /**
         * Sets the number of channels in the signal
         * @param channelCount the number of channels in the signal
         */
	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

        /**
         * Returns the number of samples in a single channel
         * @return the number of samples in a single channel
         */
	public int getSampleCount() {
		return sampleCount;
	}

        /**
         * Sets the number of samples in a single channel
         * @param sampleCount the number of samples in a single channel
         */
	public void setSampleCount(int sampleCount) {
		this.sampleCount = sampleCount;
	}
	
	public float[] getCalibrationGain() {
		return calibrationGain;
	}

	public void setCalibrationGain(float calibration) {
		if (calibrationGain == null || getChannelCount() != calibrationGain.length) {
			if (getChannelCount() > 0)
				calibrationGain = new float[getChannelCount()];
			else
				calibrationGain = new float[1];
		}
		Arrays.fill(calibrationGain, calibration);
	}

	public void setCalibrationGain(float[] calibrationGain) {
		this.calibrationGain = calibrationGain;
	}

	public float[] getCalibrationOffset() {
		return calibrationOffset;
	}

	public void setCalibrationOffset(float calibrationOffset) {
		if (this.calibrationOffset == null || getChannelCount() != this.calibrationOffset.length)
			if (getChannelCount() > 0)
				this.calibrationOffset = new float[getChannelCount()];
			else
				this.calibrationOffset = new float[1];
		Arrays.fill(this.calibrationOffset, calibrationOffset);
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

        /**
         * Returns the {@link RawSignalSampleType type} of the samples
         * @return the  type of the samples
         */
	public RawSignalSampleType getSampleType() {
		return sampleType;
	}

        /**
         * Sets the {@link RawSignalSampleType type} of the samples
         * @param sampleType the type of the samples
         */
	public void setSampleType(RawSignalSampleType sampleType) {
		this.sampleType = sampleType;
	}

        /**
         * Returns the {@link RawSignalByteOrder order} of bytes in the file
         * with the signal
         * @return the {@link RawSignalByteOrder order} of bytes in the file
         * with the signal
         */
	public RawSignalByteOrder getByteOrder() {
		return byteOrder;
	}

        /**
         * Sets the {@link RawSignalByteOrder order} of bytes in the file
         * with the signal
         * @param byteOrder the {@link RawSignalByteOrder order} of bytes in
         * the file with the signal
         */
	public void setByteOrder(RawSignalByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

        /**
         * Returns the length of the page in seconds
         * @return the length of the page in seconds
         */
	public float getPageSize() {
		return pageSize;
	}

        /**
         * Sets the length of the page in seconds
         * @param pageSize the length of the page in seconds
         */
	public void setPageSize(float pageSize) {
		this.pageSize = pageSize;
	}

        /**
         * Returns the number of blocks in one page
         * @return the number of blocks in one page
         */
	public int getBlocksPerPage() {
		return blocksPerPage;
	}

        /**
         * Sets the number of blocks in one page
         * @param blocksPerPage the number of blocks in one page
         */
	public void setBlocksPerPage(int blocksPerPage) {
		this.blocksPerPage = blocksPerPage;
	}

        /**
         * Returns an array of labels of signal channels
         * @return an array of labels of signal channels
         */
	public String[] getChannelLabels() {
		return channelLabels;
	}

        /**
         * Sets an array of labels of signal channels
         * @param channelLabels an array of labels of signal channels
         */
	public void setChannelLabels(String[] channelLabels) {
		this.channelLabels = channelLabels;
	}

        /**
         * Returns the position (in seconds) of the marker in the described
         * signal
         * @return the position (in seconds) of the marker in the described
         * signal
         */
	public double getMarkerOffset() {
		return markerOffset;
	}

        /**
         * Sets the position (in seconds) of the marker in the described
         * signal
         * @param markerOffset the position (in seconds) of the marker in the
         * described signal
         */
	public void setMarkerOffset(double markerOffset) {
		this.markerOffset = markerOffset;
	}

        /**
         * Returns the time of exporting the signal to the file
         * @return the time of exporting the signal to the file
         */
	public Date getExportDate() {
		return exportDate;
	}

        /**
         * Sets the time of exporting the signal to the file
         * @param exportDate the time of exporting the signal to the file
         */
	public void setExportDate(Date exportDate) {
		this.exportDate = exportDate;
	}

}
