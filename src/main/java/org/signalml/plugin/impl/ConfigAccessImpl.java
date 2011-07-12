package org.signalml.plugin.impl;

import java.io.File;
import java.util.ArrayList;

import org.signalml.plugin.export.config.SvarogAccessConfig;
import org.signalml.plugin.export.config.SvarogConfiguration;
import org.signalml.plugin.loader.PluginLoader;

/**
 * Svarog configuration facade (for plugins).
 *
 * @author Stanislaw Findeisen
 */
public class ConfigAccessImpl extends AbstractAccess implements SvarogAccessConfig {
    
    protected ConfigAccessImpl(PluginAccessClass parent) {
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
        PluginLoader loader = PluginLoader.getInstance();
        ArrayList<File> files = loader.getPluginDirs();
        if (files == null) throw new RuntimeException("no profile directories stored");
        File[] filesArray = new File[files.size()];
        int i = 0;
        for (File file : files){
            filesArray[i++] = new File(file.getAbsolutePath());
        }
        return filesArray;
    }

}
