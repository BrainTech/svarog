package org.signalml.plugin.newstager;

import org.signalml.plugin.export.SvarogAccess;

/**
 * Svarog i18n API delegate.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class NewStagerI18nDelegate {
	private final SvarogAccess svarogAccess;

	protected NewStagerI18nDelegate(SvarogAccess sa) {
		this.svarogAccess = sa;
	}

	/**
	 * Translates a message using Svarog i18n API.
	 *
	 * @param msg message to translate (English version)
	 * @return the translated message
	 */
	public String _(String msg) {
		return svarogAccess.getI18nAccess().translate(msg);
	}

	/**
	 * Translates a message using Svarog i18n API.
	 *
	 * @param msg message to translate and render (English version)
	 * @param arguments actual values to render
	 * @return the translated message
	 */
	public String _R(String msg, Object ... arguments) {
		return svarogAccess.getI18nAccess().translateR(msg, arguments);
	}
}
