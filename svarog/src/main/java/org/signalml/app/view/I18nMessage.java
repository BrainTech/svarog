package org.signalml.app.view;

/**
 * A locale independent string that renders itself according to the current locale.
 *
 * Typically this will use Svarog i18n API.
 *
 * @see {@link SvarogAccessI18nImpl}
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public interface I18nMessage {
	/**
	 * Returns this string value (for the current locale).
	 *
	 * @return this string value (for the current locale)
	 */
	public String i18n();
}
