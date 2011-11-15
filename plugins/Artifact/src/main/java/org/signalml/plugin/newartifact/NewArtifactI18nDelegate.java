package org.signalml.plugin.newartifact;

import org.signalml.plugin.export.PluginAuth;
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
	private PluginAuth pluginAuth;

	protected NewArtifactI18nDelegate(SvarogAccess sa, PluginAuth auth) {
		this.svarogAccess = sa;
		this.pluginAuth = auth;
	}

	/**
	 * Translates a message using Svarog i18n API.
	 *
	 * @param msg message to translate (English version)
	 * @return the translated message
	 */
	public String _(String msg) {
		return svarogAccess.getI18nAccess().translate(pluginAuth, BundleName, msg);
	}

	/**
	 * Translates a message using Svarog i18n API.
	 *
	 * @param msg message to translate and render (English version)
	 * @param arguments actual values to render
	 * @return the translated message
	 */
	public String _R(String msg, Object ... arguments) {
		return svarogAccess.getI18nAccess().translateR(pluginAuth, BundleName, msg, arguments);
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
