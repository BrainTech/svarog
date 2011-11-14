package org.signalml.plugin.impl;

import java.util.UUID;

import org.signalml.plugin.export.PluginAuth;

/**
 * PluginAuth implementation.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public final class PluginAuthImpl implements PluginAuth {
    
    private String pluginID;
    
    public PluginAuthImpl(String id) {
        super();
        this.pluginID = id;
    }

    public PluginAuthImpl(UUID id) {
        this(id.toString());
    }

    @Override
    public String getID() {
        return pluginID;
    }
    
    @Override
    public String toString() {
        return getID().toString();
    }
}
