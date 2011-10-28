/* RawSignalDocument.java created 2008-01-28
 *
 */

package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.List;

import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.codec.SignalMLCodec;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.SignalType;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleSource;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.plugin.export.SignalMLException;

/**
 * The document with the signal stored in the raw form (without
 * a {@link SignalMLCodec}).
 * Apart from the functions of {@link AbstractFileSignal} this class:
 * <ul>
 * <li>implements opening and closing this document,</li>
 * <li>contains the {@link RawSignalDescriptor descriptor} of the
 * raw signal and returns it,</li>
 * <li>returns if this document can get/set:
 * <ul><li>sampling frequency,</li>
 * <li>calibration</li>
 * <li>number of channels</li>
 * </ul>
 * and if it does allows to get and set (for number of channels only get)
 * them.
 * It is done by passing these functions to the 
 * {@link MultichannelSampleSource source} of samples.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RawSignalDocument extends AbstractFileSignal {

	/**
	 * the {@link RawSignalDescriptor descriptor} of the signal in this
	 * document
	 */
	private RawSignalDescriptor descriptor;

	/**
	 * Constructor. Sets the {@link SignalType type} and
	 * {@link RawSignalDescriptor descriptor}.
	 * @param type the type of the signal
	 * @param descriptor the descriptor of the signal.
	 */
	public RawSignalDocument(SignalType type, RawSignalDescriptor descriptor) {
		super(type);
		this.descriptor = descriptor;
	}

	@Override
	public void closeDocument() throws SignalMLException {
		if (sampleSource != null) {
			((RawSignalSampleSource) sampleSource).close();
			sampleSource = null;
		}
		super.closeDocument();
	}

	@Override
	public void openDocument() throws SignalMLException, IOException {
		if (backingFile == null) {
			throw new SignalMLException("error.noBackingFile");
		}
		RawSignalSampleSource sampleSource = new RawSignalSampleSource(backingFile.getAbsoluteFile(), descriptor.getChannelCount(), descriptor.getSamplingFrequency(), descriptor.getSampleType(), descriptor.getByteOrder());
		sampleSource.setCalibrationGain(descriptor.getCalibrationGain());
		sampleSource.setCalibrationOffset(descriptor.getCalibrationOffset());
		sampleSource.setLabels(descriptor.getChannelLabels());
		sampleSource.setFirstSampleTimestamp(descriptor.getFirstSampleTimestamp());

		this.sampleSource = sampleSource;
	}

	/**
	 * Returns the value of calibration.
	 * @return the value of calibration
	 */
	public float[] getCalibrationGain() {
		return sampleSource.getCalibrationGain();
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
	 * If the document is sampling frequency capable, sets the new value
	 * of sampling frequency.
	 * @param samplingFrequency the new value of sampling frequency
	 */
	public void setSamplingFrequency(float samplingFrequency) {
		sampleSource.setSamplingFrequency(samplingFrequency);
	}

	/**
	 * Returns an array of labels of signal channels.
	 * @return an array of labels of signal channels
	 */
	public String[] getLabels() {
		return descriptor.getChannelLabels();
	}

	/**
	 * Returns the {@link RawSignalSampleType type} of samples.
	 * @return the type of samples
	 */
	public RawSignalSampleType getSampleType() {
		return descriptor.getSampleType();
	}

	/**
	 * Returns the {@link RawSignalByteOrder order} of bytes in the file with
	 * the signal.
	 * @return the order of bytes in the file with the signal.
	 */
	public RawSignalByteOrder getByteOrder() {
		return descriptor.getByteOrder();
	}

	/**
	 * Returns the {@link RawSignalDescriptor descriptor} of the signal in this
	 * document.
	 * @return the descriptor of the signal in this document
	 */
	public RawSignalDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public String getFormatName() {
		return "InternalInterleavedRAW";
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = super.getPropertyList();

		list.add(new LabelledPropertyDescriptor("property.rawSignal.sampleType", "sampleType", RawSignalDocument.class, "getSampleType", null));
		list.add(new LabelledPropertyDescriptor("property.rawSignal.byteOrder", "byteOrder", RawSignalDocument.class, "getByteOrder", null));

		return list;

	}

}

