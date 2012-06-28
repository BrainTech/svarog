package org.signalml.domain.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;

public enum ExportFormatType {

	RAW(_("RAW"), "bin"),
	ASCII(_("ASCII"), "ascii"),
	MATLAB(_("MATLAB"), "mat"),
	EEGLab(_("EEGLab"), "set");

	/**
	 * Value which is displayed in GUI when
	 * the user needs to choose one of these
	 * export types.
	 */
	private String displayValue;

	private String defaultExtension;

	private ExportFormatType(String displayValue, String defaultExtension) {
		this.displayValue = displayValue;
		this.defaultExtension = defaultExtension;
	}

	public String getDefaultExtension() {
		return defaultExtension;
	}

	@Override
	public String toString() {
		return displayValue;
	}

}
