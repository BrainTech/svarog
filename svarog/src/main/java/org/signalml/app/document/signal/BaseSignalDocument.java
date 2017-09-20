package org.signalml.app.document.signal;

import java.io.IOException;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.plugin.export.SignalMLException;

/**
 * Base class for RAW and ASCII signal documents.
 *
 * Apart from the functions of {@link AbstractFileSignal}, this class
 * contains the {@link RawSignalDescriptor descriptor} of the signal.
 *
 * @see RawSignalDocument
 * @see AsciiSignalDocument
 * @author piotr.rozanski@braintech.pl
 */
public abstract class BaseSignalDocument extends AbstractFileSignal {

	/**
	 * the {@link RawSignalDescriptor descriptor} of the signal in this
	 * document
	 */
	private RawSignalDescriptor descriptor;

	/**
	 * Constructor. Sets the {@link RawSignalDescriptor descriptor}.
	 * @param descriptor the descriptor of the signal.
	 */
	public BaseSignalDocument(RawSignalDescriptor descriptor) {
		super();
		this.descriptor = descriptor;
	}

	@Override
	public void closeDocument() throws SignalMLException {
		if (sampleSource != null) {
			((BaseSignalSampleSource) sampleSource).close();
			sampleSource = null;
		}
		super.closeDocument();
	}

	@Override
	public void openDocument() throws SignalMLException, IOException {
		if (backingFile == null) {
			throw new SignalMLException("error.noBackingFile");
		}
		BaseSignalSampleSource theSampleSource = createSampleSource();
		theSampleSource.setCalibrationGain(descriptor.getCalibrationGain());
		theSampleSource.setCalibrationOffset(descriptor.getCalibrationOffset());
		theSampleSource.setLabels(descriptor.getChannelLabels());
		theSampleSource.setFirstSampleTimestamp(descriptor.getFirstSampleTimestamp());

		this.sampleSource = theSampleSource;
	}

	protected abstract BaseSignalSampleSource createSampleSource() throws IOException;

	/**
	 * Returns the {@link RawSignalDescriptor descriptor} of the signal in this
	 * document.
	 * @return the descriptor of the signal in this document
	 */
	public RawSignalDescriptor getDescriptor() {
		return descriptor;
	}

}
