package org.signalml.plugin.impl;

import java.text.MessageFormat;

import org.signalml.app.SvarogI18n;
import org.signalml.plugin.export.PluginAuth;
import org.signalml.plugin.export.i18n.SvarogAccessI18n;
import org.springframework.context.MessageSourceResolvable;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import org.signalml.app.logging.SvarogLogger;
import static org.signalml.util.SvarogConstants.I18nCatalogId;

/**
 * {@link SvarogAccessI18n} implementation using org.xnap.commons.i18n.* classes.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogAccessI18nImpl implements SvarogI18n, SvarogAccessI18n {
    
    /**
     * Returns the singleton instance.
     * @return
     */
    public static SvarogAccessI18nImpl getInstance() {
        return Instance;
    }
    
    private static final SvarogAccessI18nImpl Instance = new SvarogAccessI18nImpl();

    private PluginAccessClass pluginAccessClass;
    private final I18n coreI18n;
    
    private SvarogAccessI18nImpl() {
	this.coreI18n = I18nFactory.getI18n(SvarogAccessI18nImpl.class, I18nCatalogId);
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
    public String translateN(PluginAuth auth, String catalogId, String key, String keyPlural, long n) {
        String s = getI18n(auth, catalogId).trn(key, keyPlural, n);
        SvarogLogger.getSharedInstance().debug("translateN: " + key + " --> " + s);
        return s;
    }

    @Override
    public String translateR(PluginAuth auth, String catalogId, String key, Object ... arguments) {
        return render(translate(auth, catalogId, key), arguments);
    }

    @Override
    public String translateNR(PluginAuth auth, String catalogId, String key, String keyPlural, long n, Object ... arguments) {
        return render(translateN(auth, catalogId, key, keyPlural, n), arguments);
    }

    @Override
    public String _(String key) {
        return translate(null, I18nCatalogId, key);
    }

    @Override
    public String N_(String key, String keyPlural, long n) {
        return translateN(null, I18nCatalogId, key, keyPlural, n);
    }
    
    @Override
    public String _R(String key, Object ... arguments) {
    	return render(_(key), arguments);
    }
    
    @Override
    public String N_R(String key, String keyPlural, long n, Object ... arguments) {
        return render(N_(key, keyPlural, n), arguments);
    }

    @Override
    public String render(String pattern, Object ... arguments) {
    	return MessageFormat.format(pattern, arguments);
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
}
