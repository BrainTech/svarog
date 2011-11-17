package org.signalml.app;

import org.springframework.context.MessageSourceResolvable;

/**
 * Svarog core i18n interface.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public interface ISvarogI18n {
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
