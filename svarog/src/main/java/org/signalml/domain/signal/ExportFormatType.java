package org.signalml.domain.signal;

import org.apache.log4j.Logger;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.signal.export.ISignalWriter;
import org.signalml.domain.signal.export.csv.CSVSignalWriter;
import org.signalml.domain.signal.export.edf.BDFSignalWriter;
import org.signalml.domain.signal.export.edf.EDFSignalWriter;
import org.signalml.domain.signal.export.eeglab.EEGLabSignalWriter;
import org.signalml.domain.signal.export.eeglab.MatlabSignalWriter;
import org.signalml.domain.signal.raw.RawSignalWriter;

public enum ExportFormatType {

	RAW(_("RAW"), "bin", RawSignalWriter.class),
	CSV(_("CSV"), "csv", CSVSignalWriter.class),
	EDF(_("EDF+"), "edf", EDFSignalWriter.class),
	BDF(_("BDF+"), "bdf", BDFSignalWriter.class),
	MATLAB(_("MATLAB"), "mat", MatlabSignalWriter.class),
	EEGLab(_("EEGLab"), "set", EEGLabSignalWriter.class);

	protected static final Logger logger = Logger.getLogger(ExportFormatType.class);

	/**
	 * Value which is displayed in GUI when
	 * the user needs to choose one of these
	 * export types.
	 */
	private String displayValue;

	/**
	 * The default extension used by the files that are exported to a specific format.
	 */
	private String defaultExtension;

	/**
	 * The class of the signal writer that will be used to export a given format.
	 */
	private Class<? extends ISignalWriter> signalWriterClass;

	private ExportFormatType(String displayValue, String defaultExtension, Class<? extends ISignalWriter> signalWriterClass) {
		this.displayValue = displayValue;
		this.defaultExtension = defaultExtension;
		this.signalWriterClass = signalWriterClass;
	}

	public String getDefaultExtension() {
		return defaultExtension;
	}

	public ISignalWriter getSignalWriter() {

		try {
			return signalWriterClass.getConstructor(null).newInstance();
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;

	}

	@Override
	public String toString() {
		return displayValue;
	}

}
