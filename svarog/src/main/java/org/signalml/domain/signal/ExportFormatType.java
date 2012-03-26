package org.signalml.domain.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;

public enum ExportFormatType {

	RAW(_("RAW")),

	ASCII(_("ASCII")),

	EEGLab(_("EEGLab"));

	private String s;

	ExportFormatType(String s) {
		this.s = s;
	}

	@Override
	public String toString() {
		return s;
	}

	public Object[] getArguments() {
		return new Object[0];
	}

	public String[] getCodes() {
		return new String[] { "formatType." + name() };
	}

	public String getDefaultMessage() {
		return name();
	}

}
