package org.signalml.plugin.impl.config;

import java.io.File;
import java.util.ArrayList;

import org.signalml.plugin.data.PluginConfigWithMessageSourceReference;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.config.SvarogAccessConfig;
import org.signalml.plugin.export.config.SvarogConfiguration;
import org.signalml.plugin.impl.AbstractAccess;
import org.signalml.plugin.impl.PluginAccessClass;
import org.signalml.plugin.loader.PluginLoaderHi;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Svarog configuration facade (for plugins).
 *
 * @author Stanislaw Findeisen
 */
public class ConfigAccessImpl extends AbstractAccess implements SvarogAccessConfig {
    
    public ConfigAccessImpl(PluginAccessClass parent) {
        super(parent);
    }

    @Override
    public SvarogConfiguration getSvarogConfiguration() {
        return getViewerElementManager().getApplicationConfig();
    }
    
    @Override
    public File getProfileDirectory() {
        return new File(getViewerElementManager().getProfileDir().getAbsolutePath());
    }

    @Override
    public File[] getPluginDirectories() {
        PluginLoaderHi loader = PluginLoaderHi.getInstance();
        ArrayList<File> files = loader.getPluginDirs();
        if (files == null) throw new RuntimeException("no profile directories stored");
        File[] filesArray = new File[files.size()];
        int i = 0;
        for (File file : files){
            filesArray[i++] = new File(file.getAbsolutePath());
        }
        return filesArray;
    }

    @Override
    /**
     * This implementation was copied from org.signalml.plugin.i18n.PluginMessageSourceManager
     * (2011-08-18).
     */
    public MessageSourceAccessor getMessageSource() throws PluginException {
        Object resource = PluginResourceRepository.GetResource("config");

        PluginConfigWithMessageSourceReference config;
        try {
            config = (PluginConfigWithMessageSourceReference) resource;
        } catch (ClassCastException e) {
            throw new PluginException(e);
        }

        Object result = PluginResourceRepository.GetResource(
                    config.getMessageSourceName(), config.getPluginClass());
        try {
            return (MessageSourceAccessor) result;
        } catch (ClassCastException e) {
            throw new PluginException(e);
        }
    }
    
    @Override
    public Object getResource(String resourceName) throws PluginException {
        return PluginResourceRepository.GetResource(resourceName);
    }
    
    @Override
    /**
     * This implementation was copied from org.signalml.plugin.i18n.PluginMessageSourceManager
     * (2011-08-18).
     */
    public void setupConfig(Plugin plugin, String configResourceName) {
        PluginResourceRepository.RegisterPlugin(plugin.getClass(), configResourceName);
    }
}
