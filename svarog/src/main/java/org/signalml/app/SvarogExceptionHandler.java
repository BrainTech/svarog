package org.signalml.app;

import java.awt.Window;

import org.apache.log4j.Logger;
import org.signalml.app.view.components.dialogs.errors.Dialogs;

/**
 * Svarog exception handler.
 * 
 * It is a singleton.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
    protected static final Logger log = Logger.getLogger(SvarogExceptionHandler.class);

    private static SvarogExceptionHandler instance = null;
    private static boolean installed = false;

    /**
     * Installs SvarogExceptionHandler instance as default exception handler.
     *
     * @author Stanislaw Findeisen (Eisenbits)
     * @see Thread#setDefaultUncaughtExceptionHandler(java.lang.Thread.UncaughtExceptionHandler)
     */
    public static void install() {
        if (!installed) {
            synchronized (SvarogExceptionHandler.class) {
                if (!installed) {
                    Thread.setDefaultUncaughtExceptionHandler(getSharedInstance());
                    installed = true;
                    log.debug("SvarogExceptionHandler successfully installed!");
                }
            }
        }
    }

    /** Returns the shared instance. */
    protected static SvarogExceptionHandler getSharedInstance() {
        if (instance == null) {
            synchronized (SvarogExceptionHandler.class) {
                if (instance == null)
                    instance = new SvarogExceptionHandler();
            }
        }
        return instance;
    }

    private SvarogExceptionHandler() {
    }

    private void displayUserMessage(Throwable t) {
        Dialogs.showExceptionDialog((Window) null, t);
    }

    protected void handleAWT(Throwable t) {
        log.error("AWT exception handler: " + t);
        t.printStackTrace();
        displayUserMessage(t);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("uncaught exception in thread [" + (t.getId()) + "/" + (t.getName()) + "]", e);
        displayUserMessage(e);
    }
}
