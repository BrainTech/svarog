package org.signalml.plugin.impl;

import org.signalml.app.SvarogI18n;
import org.signalml.plugin.export.PluginAuth;
import org.signalml.plugin.export.i18n.SvarogAccessI18n;
import org.signalml.util.SvarogConstants;
import org.springframework.context.MessageSourceResolvable;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import org.signalml.app.logging.SvarogLogger;

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
        if (Instance == null) {
            synchronized (SvarogAccessI18nImpl.class) {
                if (Instance == null)
                    Instance = new SvarogAccessI18nImpl();
            }
        }
        return Instance;
    }
    
    private static SvarogAccessI18nImpl Instance;
    private static final String SvarogCatalogId = SvarogConstants.I18nCatalogId;
    private PluginAccessClass pluginAccessClass;
    private I18n coreI18n;
    
    private SvarogAccessI18nImpl() {
        super();
        setCoreI18n(I18nFactory.getI18n(SvarogAccessI18nImpl.class, SvarogCatalogId));
    }
    
    private Class<?> getClass(PluginAuth auth) {
        return (pluginAccessClass.getPluginHead(auth).getPluginObj().getClass());
    }
    
    private I18n getI18n(PluginAuth auth, String catalogId) {
    	SvarogLogger.getSharedInstance().debug("getI18n: " + auth + "/" + catalogId);
    	if (auth == null)
    		return getCoreI18n();
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
    public String translateN(PluginAuth auth, String catalogId, String key, String keyPlural, long n) {
        String s = getI18n(auth, catalogId).trn(key, keyPlural, n);
        SvarogLogger.getSharedInstance().debug("translateN: " + key + " --> " + s);
        return s;
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
	 * Sets {@link #coreI18n} to the given value.
	 * 
	 * @param x
	 */
	private void setCoreI18n(I18n x) {
		coreI18n = x;
	}

	/**
	 * Returns {@link #coreI18n}.
	 *
	 * @return
	 */
	private I18n getCoreI18n() {
		return coreI18n;
	}
}
