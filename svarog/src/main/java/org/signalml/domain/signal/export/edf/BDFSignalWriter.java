package org.signalml.domain.signal.export.edf;

import org.signalml.domain.signal.export.edf.writer.EDFwriter;

/**
 * Exports signal to BDF+ format.
 */
public class BDFSignalWriter extends EDFSignalWriter {

	/**
	 * Create a new exporter for BDF+ format.
	 */
	public BDFSignalWriter() {
		super(EDFwriter.EDFLIB_FILETYPE_BDFPLUS);
	}

}
