package org.signalml.app.view;

/**
 * An I18nMessage implementation that is a wrapper around a ready java.lang.String object.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class I18nMessageStringWrapper implements I18nMessage {
	private String contents;

	public I18nMessageStringWrapper(String s) {
		contents = s;
	}

	@Override
	public String i18n() {
		return contents;
	}
}
