/* SignalMLMRUDEntry.java created 2007-09-20
 *
 */

package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.util.List;

import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.codec.SignalMLCodec;
import org.signalml.plugin.export.signal.Document;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Serializable description of a file with a signal stored in it using
 * signalML {@link SignalMLCodec codec}. Contains:
 * <ul>
 * <li>name and id of the codec</li>
 * <li>sampling frequency of the signal</li>
 * <li>number of channels in the signal</li>
 * <li>size of a page of the signal (in seconds) and the number of blocks in a
 * single page</li>
 * <li>calibration of the signal</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mrud-signalml")
public class SignalMLMRUDEntry extends MRUDEntry {

	/**
	 * unique identifier of the {@link SignalMLCodec codec}
	 */
	private String codecUID;
	
	/**
	 * the format name of the {@link SignalMLCodec codec}
	 */
	private String formatName;

	/**
	 * the number of samples in one second of the signal (in a single channel)
	 */
	private Float samplingFrequency;
	
	/**
	 * the number of channels in the signal
	 */
	private Integer channelCount;
	
	/**
	 * the value of calibration
	 */
	private Float calibration;
	
	/**
	 * the size of a page of the signal in seconds
	 */
	private Float pageSize;
	
	/**
	 * the number of blocks in a single page of the signal
	 */
	private Integer blocksPerPage;

	/**
	 * Empty constructor.
	 */
	protected SignalMLMRUDEntry() {
		super();
	}

	/**
	 * Constructor. Sets:
	 * <ul>
	 * <li>the type of {@link ManagedDocumentType type} of the {@link Document
	 * document},</li>
	 * <li>the class of the document,</li>
	 * <li>the new file created on the basis of the provided path,</li>
	 * <li>the path to the file converted to the absolute path,</li>
	 * <li>the unique identifier of the {@link SignalMLCodec codec},</li>
	 * <li>the format name of the codec.</li>
	 * </ul>
	 * @param documentType the type of type of the document
	 * @param documentClass the class of the document
	 * @param path the path to the file
	 * @param codecUID the unique identifier of the codec
	 * @param formatName the format name of the codec
	 */
	public SignalMLMRUDEntry(ManagedDocumentType documentType, Class<?> documentClass, String path, String codecUID, String formatName) {
		super(documentType, documentClass, path);
		this.codecUID = codecUID;
		this.formatName = formatName;
	}

	/**
	 * Returns the unique identifier of the {@link SignalMLCodec codec}.
	 * @return the unique identifier of the codec
	 */
	public String getCodecUID() {
		return codecUID;
	}

	/**
	 * Sets the unique identifier of the {@link SignalMLCodec codec}.
	 * @param codecUID the unique identifier of the codec
	 */
	public void setCodecUID(String codecUID) {
		this.codecUID = codecUID;
	}

	/**
	 * Returns the format name of the {@link SignalMLCodec codec}.
	 * @return the format name of the codec
	 */
	public String getFormatName() {
		return formatName;
	}

	/**
	 * Sets the format name of the {@link SignalMLCodec codec}.
	 * @param formatName the format name of the codec
	 */
	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

	/**
	 * Returns the number of samples per second in a single channel.
	 * @return the sampling frequency
	 */
	public Float getSamplingFrequency() {
		return samplingFrequency;
	}

	/**
	 * Sets the number of samples per second in a single channel.
	 * @param samplingFrequency the sampling frequency
	 */
	public void setSamplingFrequency(Float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	/**
	 * Returns the number of channels in the signal.
	 * @return the number of channels in the signal
	 */
	public Integer getChannelCount() {
		return channelCount;
	}

	/**
	 * Sets the number of channels in the signal.
	 * @param channelCount the number of channels in the signal
	 */
	public void setChannelCount(Integer channelCount) {
		this.channelCount = channelCount;
	}

	/**
	 * Returns the value of calibration.
	 * @return the value of calibration
	 */
	public Float getCalibration() {
		return calibration;
	}

	/**
	 * Sets the value of calibration.
	 * @param calibration the value of calibration
	 */
	public void setCalibration(Float calibration) {
		this.calibration = calibration;
	}

	/**
	 * Returns the size (length) of a page of signal in seconds.
	 * @return the size (length) of a page
	 */
	public Float getPageSize() {
		return pageSize;
	}

	/**
	 * Sets the size (length) of a page of signal in seconds.
	 * @param pageSize the size (length) of a page
	 */
	public void setPageSize(Float pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Returns the number of blocks in a single page of the signal.
	 * @return the number of blocks in a single page of the signal
	 */
	public Integer getBlocksPerPage() {
		return blocksPerPage;
	}

	/**
	 * Sets the number of blocks in a single page of the signal.
	 * @param blocksPerPage the number of blocks in a single page of the signal
	 */
	public void setBlocksPerPage(Integer blocksPerPage) {
		this.blocksPerPage = blocksPerPage;
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {
		List<LabelledPropertyDescriptor> list = super.getPropertyList();

		list.add(2, new LabelledPropertyDescriptor("property.signalmlmrud.formatName", "formatName", SignalMLMRUDEntry.class));

		return list;

	}

}
