package org.signalml.plugin.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.impl.PluginAuthImpl;

/**
 * Plugin descriptor in Svarog.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class PluginHead {
    /** List of plugins this one depends on. */
    private ArrayList<PluginHead> dependencies = new ArrayList<PluginHead>();
    private PluginDescription description;
    private PluginLoaderLo loader;
    /** Plugin object. */
    private Plugin pluginFacade;
    private PluginAuthImpl pluginAuth;
    
    protected PluginHead(PluginDescription desc) {
        this.description = desc;
    }
    
    protected PluginHead(PluginDescription desc, PluginLoaderLo ld, Plugin pluginObj) {
        this.description = desc;
        this.loader = ld;
        this.pluginFacade = pluginObj;
    }
    
    protected void addDependency(PluginHead h) {
        dependencies.add(h);
    }
    
    protected List<PluginHead> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }
    
    protected PluginDescription getDescription() {
        return description;
    }
    
    protected boolean hasLoader() {
        return (null != loader);
    }
    protected PluginLoaderLo getLoader() {
        return loader;
    }
    protected void setLoader(PluginLoaderLo cl) {
        this.loader = cl;
    }
    
    public PluginAuthImpl getPluginAuth() {
        return pluginAuth;
    }
    protected void setPluginAuth(PluginAuthImpl auth) {
        this.pluginAuth = auth;
    }

    public Plugin getPluginObj() {
        return pluginFacade;
    }
    protected void setPluginObj(Plugin p) {
        this.pluginFacade = p;
    }
    
    public boolean containsClass(String className) {
        if (null == loader)
            return false;
        return loader.hasLoaded(className);
    }
    
    public String toString() {
        if (null == description) {
            return super.toString();
        } else {
            return description.getName();
        }
    }
}
