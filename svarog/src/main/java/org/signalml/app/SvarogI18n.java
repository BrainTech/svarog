package org.signalml.app;

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
}
