/* RawSignalDocument.java created 2008-01-28
 *
 */

package org.signalml.app.document.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.List;

import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.codec.SignalMLCodec;
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
	public RawSignalDocument(RawSignalDescriptor descriptor) {
		super();
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

	/**
	 * Returns the {@link RawSignalSampleType type} of samples.
	 *
	 * @return the type of samples
	 */
	public RawSignalSampleType getSampleType() {
		return descriptor.getSampleType();
	}

	/**
	 * Returns the {@link RawSignalByteOrder order} of bytes in the file with
	 * the signal.
	 *
	 * @return the order of bytes in the file with the signal.
	 */
	public RawSignalByteOrder getByteOrder() {
		return descriptor.getByteOrder();
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = super.getPropertyList();

		list.add(new LabelledPropertyDescriptor(_("sample type"), "sampleType", RawSignalDocument.class, "getSampleType", null));
		list.add(new LabelledPropertyDescriptor(_("byte order"), "byteOrder", RawSignalDocument.class, "getByteOrder", null));

		return list;

	}

}

