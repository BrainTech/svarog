package org.signalml.plugin.newartifact;

import org.signalml.plugin.export.SvarogAccess;
import org.springframework.context.MessageSourceResolvable;

/**
 * Svarog i18n API delegate.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class NewArtifactI18nDelegate {
	private static final String BundleName = "org.signalml.plugin.newartifact.i18n.I18nBundle";
	private SvarogAccess svarogAccess;

	protected NewArtifactI18nDelegate(SvarogAccess sa) {
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

	/**
	 * A workaround for the old code.
	 *
	 * @param x
	 * @return x.getDefaultMessage()
	 */
	@Deprecated
	public String getMessage(MessageSourceResolvable x) {
		return x.getDefaultMessage();
	}
}
