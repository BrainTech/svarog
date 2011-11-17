package org.signalml.app;

import org.springframework.context.MessageSourceResolvable;

/**
 * Svarog core i18n interface.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public interface ISvarogI18n {
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
