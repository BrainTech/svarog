package org.signalml.plugin.impl;

import org.signalml.plugin.export.PluginAuth;
import org.signalml.plugin.export.i18n.SvarogAccessI18n;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * {@link SvarogAccessI18n} implementation using org.xnap.commons.i18n.* classes.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogAccessI18nImpl implements SvarogAccessI18n {
    
    private PluginAccessClass pluginAccessClass;
    
    protected SvarogAccessI18nImpl(PluginAccessClass pac) {
        super();
        this.pluginAccessClass = pac;
    }
    
    private Class<?> getClass(PluginAuth auth) {
        if (null == auth)
            return SvarogAccessI18nImpl.class;
        else
            return (pluginAccessClass.getPluginHead(auth).getPluginObj().getClass());
    }
    
    private I18n getI18n(PluginAuth auth, String catalogId) {
        return I18nFactory.getI18n(getClass(auth), catalogId);
    }

    /**
     * Translation method.
     */
    @Override
    public String translate(PluginAuth auth, String catalogId, String key) {
        return getI18n(auth, catalogId).tr(key);
    }

    /**
     * Translation method (plural version).
     */
    @Override
    public String translateN(PluginAuth auth, String catalogId, String key, String keyPlural, long n) {
        return getI18n(auth, catalogId).trn(key, keyPlural, n);
    }

    @Override
    public String _(PluginAuth auth, String catalogId, String key) {
        return translate(auth, catalogId, key);
    }

    @Override
    public String N_(PluginAuth auth, String catalogId, String key, String keyPlural, long n) {
        return translateN(auth, catalogId, key, keyPlural, n);
    }
}
