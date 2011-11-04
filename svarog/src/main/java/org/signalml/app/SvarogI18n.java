package org.signalml.app;

import org.springframework.context.MessageSourceResolvable;

/**
 * Svarog core i18n interface.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public interface SvarogI18n {
	/**
	 * Translates the message for the specified key using the current Svarog locale.
	 *
	 * @param key English version of the message
	 * @return i18n version of the message (depending on the current Svarog locale),
	 *         or key if not found
	 */
	public String _(String key);

	/**
	 * Translates the message for the specified key using the current Svarog locale.
	 *
	 * @param key English version of the message
	 * @param keyPlural English version of the message (plural form)
	 * @param n tells "how many" and is used to select the correct plural form
	 *        (there may be more than 2)
	 * @return i18n version of the message (depending on the current Svarog locale and n),
	 *         or keyPlural if not found
	 */
	public String N_(String key, String keyPlural, long n);

	/**
	 * Translates the message for the specified key using the current Svarog locale
	 * and renders it using actual values.
	 * 
	 * @param msgKey English version of the message
	 * @param arguments actual values to place in the message
	 * @return i18n version of the message (depending on the current Svarog locale),
	 *         with arguments rendered in, or key if not found
	 */
	public String _R(String key, Object ... arguments);

	/**
	 * Translates the message for the specified key using the current Svarog locale
	 * and renders it using actual values.
	 *
	 * @param key English version of the message
	 * @param keyPlural English version of the message (plural form)
	 * @param n tells "how many" and is used to select the correct plural form
	 *        (there may be more than 2)
	 * @param arguments actual values to place in the message
	 * @return i18n version of the message (depending on the current Svarog locale and n),
	 *         with arguments rendered in, or keyPlural if not found
	 */
	public String N_R(String key, String keyPlural, long n, Object ... arguments);

	/**
	 * Just renders the given pattern using actual values.
	 * Pattern is used as-is (no translation!).
	 * 
	 * @param pattern message string with placeholders like {0}
	 * @param arguments values to render into the placeholders
	 * @return
	 * @see java.text.MessageFormat.format
	 */
	public String render(String pattern, Object ... arguments);

	/**
	 * Temporary workaround (for the old code).
	 *
	 * @param msgKey message key
	 * @return msgKey
	 */
	@Deprecated
	public String getMessage(String msgKey);

	/**
	 * Temporary workaround (for the old code).
	 *
	 * @param msgKey message key
	 * @param defaultMessage default message
	 * @return defaultMessage
	 */
	@Deprecated
	public String getMessage(String msgKey, String defaultMessage);

	/**
	 * Temporary workaround (for the old code).
	 *
	 * @param source MessageSourceResolvable instance
	 * @return source.getDefaultMessage()
	 */
	@Deprecated
	public String getMessage(MessageSourceResolvable source);
}
