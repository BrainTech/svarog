/* MP5RuntimeParameters.java created 2008-01-31
 *
 */

package org.signalml.method.mp5;

import java.io.File;

/** MP5RuntimeParameters
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5RuntimeParameters {

	private File signalFile;
	private File outputDirectory;
	private String resultFileExtension;
	private MP5SignalFormatType dataFormat;
	private MP5WritingModeType writingMode = MP5WritingModeType.CREATE;
	private int headerSize = 0;
	private int footerSize = 0;
	private int channelCount = 1;
	private int[] chosenChannels = new int[] {1};
	private int minOffset = -1;
	private int maxOffset = -1;
	private int segementSize;
	private float samplingFrequency = 128F;
	private float pointsPerMicrovolt = 1F;

	public File getSignalFile() {
		return signalFile;
	}

	public void setSignalFile(File signalFile) {
		this.signalFile = signalFile;
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public String getResultFileExtension() {
		return resultFileExtension;
	}

	public void setResultFileExtension(String resultFileExtension) {
		this.resultFileExtension = resultFileExtension;
	}

	public MP5SignalFormatType getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(MP5SignalFormatType dataFormat) {
		this.dataFormat = dataFormat;
	}

	public MP5WritingModeType getWritingMode() {
		return writingMode;
	}

	public void setWritingMode(MP5WritingModeType writingMode) {
		this.writingMode = writingMode;
	}

	public int getHeaderSize() {
		return headerSize;
	}

	public void setHeaderSize(int headerSize) {
		this.headerSize = headerSize;
	}

	public int getFooterSize() {
		return footerSize;
	}

	public void setFooterSize(int footerSize) {
		this.footerSize = footerSize;
	}

	public int getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	public int[] getChosenChannels() {
		return chosenChannels;
	}

	public void setChosenChannels(int[] chosenChannels) {
		this.chosenChannels = chosenChannels;
	}

	public int getSegementSize() {
		return segementSize;
	}

	public void setSegementSize(int segementSize) {
		this.segementSize = segementSize;
	}

	public float getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency(float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	public float getPointsPerMicrovolt() {
		return pointsPerMicrovolt;
	}

	public void setPointsPerMicrovolt(float pointsPerMicrovolt) {
		this.pointsPerMicrovolt = pointsPerMicrovolt;
	}

	public int getMinOffset() {
		return minOffset;
	}

	public void setMinOffset(int minOffset) {
		this.minOffset = minOffset;
	}

	public int getMaxOffset() {
		return maxOffset;
	}

	public void setMaxOffset(int maxOffset) {
		this.maxOffset = maxOffset;
	}

}
