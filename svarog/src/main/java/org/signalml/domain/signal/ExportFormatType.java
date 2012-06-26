package org.signalml.domain.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;

public enum ExportFormatType {

	RAW(_("RAW")),
	ASCII(_("ASCII")),
	EEGLab(_("EEGLab"));

	/**
	 * Value which is displayed in GUI when
	 * the user needs to choose one of these
	 * export types.
	 */
	private String displayValue;

	private ExportFormatType(String displayValue) {
		this.displayValue = displayValue;
	}

	@Override
	public String toString() {
		return displayValue;
	}

}
