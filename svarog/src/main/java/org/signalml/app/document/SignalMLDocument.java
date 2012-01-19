/* SignalMLDocument.java created 2007-09-18
 *
 */

package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.List;

import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecReader;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.SignalMLCodecSampleSource;
import org.signalml.plugin.export.SignalMLException;

/**
 * The document with the signal stored using a {@link SignalMLCodec codec}.
 * Apart from the functions of {@link AbstractFileSignal} this class:
 * <ul>
 * <li>implements opening and closing this document - to do it uses the
 * {@link SignalMLCodecReader reader} based on a codec,</li>
 * <li>returns if this document can get/set:
 * <ul><li>sampling frequency,</li>
 * <li>calibration</li>
 * <li>number of channels</li>
 * </ul>
 * and if it does allows to get and set them.
 * It is done by passing these functions to the 
 * {@link MultichannelSampleSource source} of samples.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLDocument extends AbstractFileSignal {

	
	/**
	 * the {@link SignalMLCodecReader reader} of signal from the file
	 */
	private SignalMLCodecReader reader;

	/**
	 * Constructor. Sets the {@link SignalMLCodecReader reader} and the
	 * {@link SignalType type} of the signal.
	 * @param reader the reader of the signal
	 * @param type the type of the signal
	 */
	public SignalMLDocument(SignalMLCodecReader reader) {
		super();
		this.reader = reader;
	}

	/**
	 * Returns the {@link SignalMLCodecReader reader} of the signal.
	 * @return the reader of the signal
	 */
	public SignalMLCodecReader getReader() {
		return reader;
	}

	@Override
	public void closeDocument() throws SignalMLException {
		reader.close();
		super.closeDocument();
	}

	@Override
	public void openDocument() throws SignalMLException, IOException {
		if (backingFile == null) {
			throw new SignalMLException("error.noBackingFile");
		}
		reader.open(backingFile.getAbsolutePath());
		sampleSource = new SignalMLCodecSampleSource(reader);
	}

	/**
	 * Returns the value of calibration.
	 * @return the value of calibration
	 */
	public float getCalibration() {
		return sampleSource.getSingleCalibrationGain();
	}

	/**
	 * Returns if this document is capable of returning a calibration
	 * @return {@code true} if this document is capable of returning
	 * a calibration, {@code false} otherwise
	 */
	public boolean isCalibrationCapable() {
		return sampleSource.isCalibrationCapable();
	}

	/**
	 * Returns if this document is capable of returning a channel count.
	 * @return {@code true} if this document is capable of returning a channel
	 * count, {@code false} otherwise
	 */
	public boolean isChannelCountCapable() {
		return sampleSource.isChannelCountCapable();
	}

	/**
	 * Returns if this document is capable of returning a sampling frequency.
	 * @return {@code true} if this document is capable of returning a sampling
	 * frequency, {@code false} otherwise
	 */
	public boolean isSamplingFrequencyCapable() {
		return sampleSource.isSamplingFrequencyCapable();
	}

	/**
	 * If the document is calibration capable, sets the new value
	 * of calibration.
	 * @param calibration the new value of calibration
	 */
	public void setCalibration(float calibration) {
		sampleSource.setCalibrationGain(calibration);
	}

	/**
	 * If the document is calibration capable, sets the new number of channels.
	 * @param channelCount the new number of channels
	 */
	public void setChannelCount(int channelCount) {
		sampleSource.setChannelCount(channelCount);
	}

	/**
	 * If the document is sampling frequency capable, sets the new value
	 * of sampling frequency.
	 * @param samplingFrequency the new value of sampling frequency
	 */
	public void setSamplingFrequency(float samplingFrequency) {
		sampleSource.setSamplingFrequency(samplingFrequency);
	}

	@Override
	public String getFormatName() {
		return reader.getCodec().getFormatName();
	}

	/**
	 * Returns the {@link SignalMLCodec#getSourceUID() UID} of the codec.
	 * @return the UID of the codec.
	 */
	public String getSourceUID() {
		return reader.getCodec().getSourceUID();
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = super.getPropertyList();

		list.add(new LabelledPropertyDescriptor("property.signalmldocument.formatName", "formatName", SignalMLDocument.class, "getFormatName", null));
		list.add(new LabelledPropertyDescriptor("property.signaldocument.calibrationCapable", "caligrationCapable", SignalMLDocument.class, "isCalibrationCapable", null));
		list.add(new LabelledPropertyDescriptor("property.signaldocument.calibration", "calibration", SignalMLDocument.class));

		return list;

	}

}

