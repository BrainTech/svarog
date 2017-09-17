package org.signalml.app.document.signal;

import java.io.IOException;
import org.signalml.domain.signal.raw.RawSignalDescriptor;

/**
 * The document with the signal stored in ASCII form.
 * This is a simple subclass derived from BaseSignalDocument,
 * designed to be used with {@link AsciiSignalSampleSource}.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class AsciiSignalDocument extends BaseSignalDocument {

	public AsciiSignalDocument(RawSignalDescriptor descriptor) {
		super(descriptor);
	}

	@Override
	protected BaseSignalSampleSource createSampleSource() throws IOException {
		return new AsciiSignalSampleSource(
			getBackingFile().getAbsoluteFile(),
			getDescriptor().getChannelCount(),
			getDescriptor().getSamplingFrequency()
		);
	}

	@Override
	public String getFormatName() {
		return "ASCII";
	}

}
