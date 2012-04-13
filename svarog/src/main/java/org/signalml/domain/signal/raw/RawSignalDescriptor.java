/* RawSignalDescriptor.java created 2008-01-18
 *
 */

package org.signalml.domain.signal.raw;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.Arrays;

import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.domain.montage.system.EegSystemName;

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
public class RawSignalDescriptor extends AbstractOpenSignalDescriptor {

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
	 * the number of samples in a single channel
	 */
	private int sampleCount;

	/**
	 * the {@link RawSignalSampleType type} of the samples
	 */
	private RawSignalSampleType sampleType;
	private RawSignalByteOrder byteOrder;

	/**
	 * The name of the {@link EegSystem EEG system} that is used
	 * for this signal.
	 */
	private EegSystemName eegSystemName;

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
	 * The timestamp of the first sample in the signal.
	 */
	private double firstSampleTimestamp = Double.NaN;

	/**
	 * Whether the signal is a backup and was not saved as a result
	 * of a proper recording stop.
	 */
	private boolean isBackup;

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
	 * Whether the signal is a backup.
	 * @return true if signal is a backup
	 */
	public boolean isBackup() {
		return isBackup;
	}

	/**
	 * Set backup info.
	 * @param isBackup backup info
	 */
	public void setIsBackup(boolean isBackup) {
		this.isBackup = isBackup;
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
		return signalParameters.getSamplingFrequency();
	}

	/**
	 * Sets the number of samples per second
	 * @param samplingFrequency the number of samples per second
	 */
	public void setSamplingFrequency(float samplingFrequency) {
		signalParameters.setSamplingFrequency(samplingFrequency);
	}

	/**
	 * Returns the number of channels in the signal
	 * @return the number of channels in the signal
	 */
	public int getChannelCount() {
		return signalParameters.getChannelCount();
	}

	/**
	 * Sets the number of channels in the signal
	 * @param channelCount the number of channels in the signal
	 */
	public void setChannelCount(int channelCount) {
		signalParameters.setChannelCount(channelCount);
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
		return signalParameters.getCalibrationGain();
	}

	public void setCalibrationGain(float calibration) {
		float[] calibrationGain = signalParameters.getCalibrationGain();
		if (calibrationGain == null || getChannelCount() != calibrationGain.length) {
			if (getChannelCount() > 0)
				calibrationGain = new float[getChannelCount()];
			else
				calibrationGain = new float[1];
		}
		Arrays.fill(calibrationGain, calibration);
		signalParameters.setCalibrationGain(calibrationGain);
	}

	public void setCalibrationGain(float[] calibrationGain) {
		signalParameters.setCalibrationGain(calibrationGain);
	}

	public float[] getCalibrationOffset() {
		return signalParameters.getCalibrationOffset();
	}

	/**
	 * Sets a value of calibration offset for this signal (same for every
	 * channel.
	 * @param offset new value of calibration offset for
	 * all channels in the signal
	 */
	public void setCalibrationOffset(float offset) {
		float[] calibrationOffset = signalParameters.getCalibrationOffset();
		if (calibrationOffset == null || getChannelCount() != calibrationOffset.length)
			if (getChannelCount() > 0)
				calibrationOffset = new float[getChannelCount()];
			else
				calibrationOffset = new float[1];
		Arrays.fill(calibrationOffset, offset);
		signalParameters.setCalibrationGain(calibrationOffset);
	}

	/**
	 * Sets a new value of calibration offset for each channel.
	 * @param calibrationOffset new value of calibration offset
	 */
	public void setCalibrationOffset(float[] calibrationOffset) {
		signalParameters.setCalibrationOffset(calibrationOffset);
	}

	public Float getMinimumValue() {
		return signalParameters.getMinimumValue();
	}

	public void setMinimumValue(Float minimumValue) {
		signalParameters.setMinimumValue(minimumValue);
	}

	public Float getMaximumValue() {
		return signalParameters.getMaximumValue();
	}

	public void setMaximumValue(Float maximumValue) {
		signalParameters.setMaximumValue(maximumValue);
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
		return signalParameters.getPageSize();
	}

	/**
	 * Sets the length of the page in seconds
	 * @param pageSize the length of the page in seconds
	 */
	public void setPageSize(float pageSize) {
		signalParameters.setPageSize(pageSize);
	}

	/**
	 * Returns the number of blocks in one page
	 * @return the number of blocks in one page
	 */
	public int getBlocksPerPage() {
		return signalParameters.getBlocksPerPage();
	}

	/**
	 * Sets the number of blocks in one page
	 * @param blocksPerPage the number of blocks in one page
	 */
	public void setBlocksPerPage(int blocksPerPage) {
		signalParameters.setBlocksPerPage(blocksPerPage);
	}

	/**
	 * Returns the name of the {@link EegSystem EEG system} that
	 * is used for this signal.
	 * @return the name of the EEG system
	 */
	public EegSystemName getEegSystemName() {
		return eegSystemName;
	}

	/**
	 * Sets the name of the {@link EegSystem} that is used for this signal.
	 * @param eegSystemName the name of the EEG system
	 */
	public void setEegSystemName(EegSystemName eegSystemName) {
		this.eegSystemName = eegSystemName;
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

	/**
	 * Returns the timestamp of the first sample for this signal.
	 * @return the timestamp of the first sample in this signal (NaN if
	 * the timestamp was not set).
	 */
	public double getFirstSampleTimestamp() {
		return firstSampleTimestamp;
	}

	/**
	 * Sets the timestamp of the first sample for this signal.
	 * @param value new value of the timestamp
	 */
	public void setFirstSampleTimestamp(double value) {
		this.firstSampleTimestamp = value;
	}

	@Override
	public void setMontage(Montage montage) {
		super.setMontage(montage);

		EegSystem eegSystem = montage.getEegSystem();
		if (eegSystem != null)
			setEegSystemName(eegSystem.getEegSystemName());
	}

	@Override
	public void setEegSystem(EegSystem eegSystem) {
		super.setEegSystem(eegSystem);
		if (eegSystem != null)
			this.setEegSystemName(eegSystem.getEegSystemName());
		else
			this.setEegSystemName(null);
	}

}
