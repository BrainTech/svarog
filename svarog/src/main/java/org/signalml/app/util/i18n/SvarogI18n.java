package org.signalml.app.util.i18n;

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.signalml.plugin.export.i18n.SvarogAccessI18n;
import org.signalml.util.SvarogConstants;
import org.springframework.context.MessageSourceResolvable;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * {@link ISvarogI18n} implementation using org.xnap.commons.i18n.* classes.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogI18n implements ISvarogI18n, SvarogAccessI18n {
	protected static final Logger log = Logger.getLogger(SvarogI18n.class);

	private final I18n i18n;

	/**
	 * Initializes i18n resources using classloader of klass, from catalog
	 * called catalogId.
	 */
	public SvarogI18n(Class klass, String catalogId) {
		log.info("loading i18n bundle " + catalogId + "for klass " + klass.getName());
		this.i18n = I18nFactory.getI18n(klass, catalogId, Locale.getDefault(),
										I18nFactory.READ_PROPERTIES|I18nFactory.FALLBACK);
	}

	/**
	 * Call {@link SvarogI18n(Class klass, String catalogId)} with catalogId
	 * set to the namespace of klass.
	 */
	public SvarogI18n(Class klass) {
		this(klass, klass.getPackage().getName());
	}

	/**
	 * Translation method.
	 */
	@Override
	public String translate(String key) {
		String s = this.i18n.tr(key);
		log.debug("translate: " + key + " --> " + s);
		return s;
	}

	/**
	 * Translation method (plural version).
	 */
	@Override
	public String translateN(String singular, String plural, long n) {
		String s = i18n.trn(singular, plural, n);
		log.debug("translateN: " + singular + " --> " + s);
		return s;
	}

	@Override
	public String translateR(String key, Object ... arguments) {
		return render(translate(key), arguments);
	}

	@Override
	public String translateNR(String singular, String plural, long n, Object... arguments) {
		return render(translateN(singular, plural, n), arguments);
	}

	@Override
	@Deprecated
	public String getMessage(MessageSourceResolvable source) {
		return source.getDefaultMessage();
	}

	@Override
	@Deprecated
	public String getMessage(String msgKey) {
		return msgKey;
	}

	@Override
	@Deprecated
	public String getMessage(String msgKey, String defaultMessage) {
		return defaultMessage;
	}


	/**
	 * Returns the singleton instance.
	 * @return
	 */
	protected static SvarogI18n getInstance() {
		return Instance;
	}

	private static final SvarogI18n Instance =
		new SvarogI18n(SvarogI18n.class, SvarogConstants.I18nCatalogId);


	/************************************************************
	 ***********	  public static parts	  *******************
	 ************************************************************/

	/**
	 * Translates the message for the specified key using the current Svarog locale.
	 *
	 * @param key English version of the message
	 * @return i18n version of the message (depending on the current Svarog locale),
	 *	   or key if not found
	 */
	public static String _(String key) {
		return getInstance().translate(key);
	}

	/**
	 * Translates the message for the specified key using the current Svarog locale.
	 *
	 * @param key English version of the message
	 * @param keyPlural English version of the message (plural form)
	 * @param n tells "how many" and is used to select the correct plural form
	 *	  (there may be more than 2)
	 * @return i18n version of the message (depending on the current Svarog locale and n),
	 *	   or keyPlural if not found
	 */
	public static String N_(String singular, String plural, long n) {
		return getInstance().translateN(singular, plural, n);
	}

	/**
	 * Translates the message for the specified key using the current Svarog locale
	 * and renders it using actual values.
	 *
	 * @param msgKey English version of the message
	 * @param arguments actual values to place in the message
	 * @return i18n version of the message (depending on the current Svarog locale),
	 *	   with arguments rendered in, or key if not found
	 */
	public static String _R(String key, Object... arguments) {
		return render(_(key), arguments);
	}

	/**
	 * Translates the message for the specified key using the current Svarog locale
	 * and renders it using actual values.
	 *
	 * @param key English version of the message
	 * @param keyPlural English version of the message (plural form)
	 * @param n tells "how many" and is used to select the correct plural form
	 *	  (there may be more than 2)
	 * @param arguments actual values to place in the message
	 * @return i18n version of the message (depending on the current Svarog locale and n),
	 *	   with arguments rendered in, or keyPlural if not found
	 */
	public static String N_R(String singular, String plural, long n, Object ... arguments) {
		return render(N_(singular, plural, n), arguments);
	}

	/**
	 * Just renders the given pattern using actual values.
	 * Pattern is used as-is (no translation!).
	 *
	 * @param pattern message string with placeholders like {0}
	 * @param arguments values to render into the placeholders
	 * @return
	 * @see java.text.MessageFormat.format
	 */
	public static String render(String pattern, Object ... arguments) {
		return MessageFormat.format(pattern, arguments);
	}

	/**
	 * Sets the locale that will be used by this I18N module.
	 * @param locale the locale to be used.
	 */
	public static void setLocale(Locale locale) {
		SvarogI18n.getInstance().i18n.setLocale(locale);
	}
}
