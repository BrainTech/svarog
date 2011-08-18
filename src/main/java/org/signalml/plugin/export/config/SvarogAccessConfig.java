package org.signalml.plugin.export.config;

import java.io.File;

import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.config.SvarogConfiguration;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Svarog configuration facade (for plugins).
 *
 * @author Stanislaw Findeisen
 */
public interface SvarogAccessConfig {
    SvarogConfiguration getSvarogConfiguration();
    
    /**
     * Returns the new file object pointing to profile directory.
     * @return the profile directory
     */
    File getProfileDirectory();
    
    /**
     * Returns an array containing plug-in directories.
     * @return an array containing plug-in directories
     */
    File[] getPluginDirectories();
    
    /**
     * Returns the message source accessor for the context plugin (stack inspection).
     * 
     * TODO return an interface instead of MessageSourceAccessor
     * 
     * @return
     * @throws PluginException
     */
    MessageSourceAccessor getMessageSource() throws SignalMLException;
    
    /**
     * Returns the resource for the given name and the context plugin (stack inspection).
     * @return
     * @throws PluginException
     */
    Object getResource(String resourceName) throws SignalMLException;
    
    /**
     * Allows plugin to make Svarog aware of this plugin config resource. configResourceName
     * can be something like this: "classpath:/org/signalml/plugin/newartifact/resource/config.xml" .
     * 
     * @param plugin
     * @param configResourceName
     */
    void setupConfig(Plugin plugin, String configResourceName);
}
