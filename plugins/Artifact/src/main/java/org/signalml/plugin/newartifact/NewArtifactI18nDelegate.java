package org.signalml.plugin.newartifact;

import org.signalml.plugin.export.PluginAuth;
import org.signalml.plugin.export.SvarogAccess;
import org.springframework.context.MessageSourceResolvable;

/**
 * Svarog i18n API delegator.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class NewArtifactI18nDelegate {
	private SvarogAccess svarogAccess;
	private PluginAuth pluginAuth;

	protected NewArtifactI18nDelegate(SvarogAccess sa, PluginAuth auth) {
		this.svarogAccess = sa;
		this.pluginAuth = auth;
	}

	/**
	 * Translates a message using Svarog i18n API.
	 *
	 * @param msg message to translate
	 * @return the translated message
	 */
	public String _(String msg) {
		return svarogAccess.getI18nAccess().translate(pluginAuth, "org.signalml.plugin.newartifact.i18n.I18nBundle", msg);
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
