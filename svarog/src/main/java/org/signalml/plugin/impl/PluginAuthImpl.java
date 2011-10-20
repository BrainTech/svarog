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
    public boolean equals(Object another) {
        if (null == another)
            return false;
        if (another == this)
            return true;
        if (another instanceof PluginAuthImpl) {
            return (getID().equals(((PluginAuthImpl) another).getID()));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return pluginID.hashCode();
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
