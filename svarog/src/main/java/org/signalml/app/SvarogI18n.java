package org.signalml.app;

import java.text.MessageFormat;

import org.signalml.app.SvarogI18n;
import org.signalml.plugin.export.PluginAuth;
import org.signalml.plugin.export.i18n.SvarogAccessI18n;
import org.springframework.context.MessageSourceResolvable;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import org.signalml.app.logging.SvarogLogger;
import static org.signalml.util.SvarogConstants.I18nCatalogId;
import org.signalml.plugin.impl.PluginAccessClass;

/**
 * {@link ISvarogI18n} implementation using org.xnap.commons.i18n.* classes.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogI18n implements ISvarogI18n, SvarogAccessI18n {

	private PluginAccessClass pluginAccessClass;
	private final I18n coreI18n;

	private SvarogI18n() {
		this.coreI18n = I18nFactory.getI18n(SvarogI18n.class, I18nCatalogId);
	}

	private Class<?> getClass(PluginAuth auth) {
		return (pluginAccessClass.getPluginHead(auth).getPluginObj().getClass());
	}

	private I18n getI18n(PluginAuth auth, String catalogId) {
		if (auth == null)
			return this.coreI18n;
		else
			return I18nFactory.getI18n(getClass(auth), catalogId);
	}

	/**
	 * Translation method.
	 */
	@Override
	public String translate(PluginAuth auth, String catalogId, String key) {
		String s = getI18n(auth, catalogId).tr(key);
		SvarogLogger.getSharedInstance().debug("translate: " + key + " --> " + s);
		return s;
	}

	/**
	 * Translation method (plural version).
	 */
	@Override
	public String translateN(PluginAuth auth, String catalogId, String singular, String plural, long n) {
		String s = getI18n(auth, catalogId).trn(singular, plural, n);
		SvarogLogger.getSharedInstance().debug("translateN: " + singular + " --> " + s);
		return s;
	}

	@Override
	public String translateR(PluginAuth auth, String catalogId, String key, Object ... arguments) {
		return render(translate(auth, catalogId, key), arguments);
	}

	@Override
	public String translateNR(PluginAuth auth, String catalogId, String singular, String plural, long n, Object ... arguments) {
		return render(translateN(auth, catalogId, singular, plural, n), arguments);
	}

	public void setPluginAccessClass(PluginAccessClass pac) {
		this.pluginAccessClass = pac;
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

	private static final SvarogI18n Instance = new SvarogI18n();


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
		return getInstance().translate(null, I18nCatalogId, key);
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
		return getInstance().translateN(null, I18nCatalogId, singular, plural, n);
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
	public static String _R(String key, Object ... arguments) {
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
}
