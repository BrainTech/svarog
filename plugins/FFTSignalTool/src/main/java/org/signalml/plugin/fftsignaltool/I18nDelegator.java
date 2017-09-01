package org.signalml.plugin.fftsignaltool;

import org.signalml.plugin.export.SvarogAccess;
import org.springframework.context.MessageSourceResolvable;

/**
 * Svarog i18n API delegator.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class I18nDelegator {
	private SvarogAccess svarogAccess;

	protected I18nDelegator(SvarogAccess sa) {
		this.svarogAccess = sa;
	}

	/**
	 * Translates a message using Svarog i18n API.
	 *
	 * @param msg message to translate
	 * @return the translated message
	 */
	public String _(String msg) {
		return svarogAccess.getI18nAccess().translate(msg);
	}

	/**
	 * Translates and renders a message using Svarog i18n API.
	 *
	 * @param msg message to translate
	 * @param arguments the values to render
	 * @return the translated message with values filled in
	 */
	public String _R(String msg, Object ... arguments) {
		return svarogAccess.getI18nAccess().translateR(msg, arguments);
	}

	/**
	 * A workaround for the old code.
	 *
	 * @param msgKey
	 * @return msgKey
	 */
	@Deprecated
	public String getMessage(String msgKey) {
		return msgKey;
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
