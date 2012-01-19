package org.signalml.plugin.impl;

import org.signalml.app.view.workspace.ViewerElementManager;

/**
 * Methods and tasks in Svarog core (facade for Svarog plugins).
 *
 * @author Stanislaw Findeisen
 */
public class AbstractAccess {
    
    /**
     * the manager of the elements of Svarog
     */
    private ViewerElementManager viewerElementManager;
    
    protected void setViewerElementManager(ViewerElementManager manager) {
        this.viewerElementManager = manager;
    }
    
    protected ViewerElementManager getViewerElementManager() {
        return viewerElementManager;
    }

    protected boolean hasViewerElementManager() {
        return (null != viewerElementManager);
    }
}
