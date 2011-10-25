package org.signalml.plugin.impl;

import org.signalml.app.SvarogI18n;
import org.signalml.plugin.export.PluginAuth;
import org.signalml.plugin.export.i18n.SvarogAccessI18n;
import org.signalml.util.SvarogConstants;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * {@link SvarogAccessI18n} implementation using org.xnap.commons.i18n.* classes.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogAccessI18nImpl implements SvarogI18n, SvarogAccessI18n {
    
    /**
     * Returns the shared instance.
     * @return
     */
    public static SvarogAccessI18nImpl getInstance() {
        if (null == Instance) {
            synchronized (SvarogAccessI18nImpl.class) {
                if (null == Instance)
                    Instance = new SvarogAccessI18nImpl();
            }
        }
        return Instance;
    }
    
    private static SvarogAccessI18nImpl Instance;
    private static final String SvarogCatalogId = SvarogConstants.I18nCatalogId;
    private PluginAccessClass pluginAccessClass;
    
    private SvarogAccessI18nImpl() {
        super();
    }
    
    private Class<?> getClass(PluginAuth auth) {
        return (pluginAccessClass.getPluginHead(auth).getPluginObj().getClass());
    }
    
    private I18n getI18n(PluginAuth auth, String catalogId) {
    	if (null == auth)
    		return I18nFactory.getI18n(SvarogAccessI18nImpl.class, SvarogCatalogId);
    	else
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
    public String _(String key) {
        return translate(null, SvarogCatalogId, key);
    }

    @Override
    public String N_(String key, String keyPlural, long n) {
        return translateN(null, SvarogCatalogId, key, keyPlural, n);
    }

    protected void setPluginAccessClass(PluginAccessClass pac) {
        this.pluginAccessClass = pac;
    }
}
