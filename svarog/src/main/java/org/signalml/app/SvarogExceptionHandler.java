package org.signalml.app;

import java.awt.Window;

import org.signalml.app.logging.SvarogLogger;
import org.signalml.app.view.dialog.ErrorsDialog;

/**
 * Svarog exception handler.
 * 
 * It is a singleton.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {

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
                    SvarogLogger.getSharedInstance().debug("SvarogExceptionHandler successfully installed!");
                }
            }
        }
    }

    /** Returns the shared instance. */
    protected static SvarogExceptionHandler getSharedInstance() {
        if (null == instance) {
            synchronized (SvarogExceptionHandler.class) {
                if (null == instance)
                    instance = new SvarogExceptionHandler();
            }
        }
        return instance;
    }

    private SvarogExceptionHandler() {
    }

    private void displayUserMessage(Throwable t) {
        ErrorsDialog.showImmediateExceptionDialog((Window) null, t);
        // String message = t.getMessage();
        //
        // if (message == null || message.length() == 0) {
        // message = "Fatal: " + t.getClass();
        // }
        //
        // JOptionPane.showMessageDialog(null, "General Error", message,
        // JOptionPane.ERROR_MESSAGE);
    }

    protected void handleAWT(Throwable t) {
        SvarogLogger.getSharedInstance().error("SvarogExceptionHandler.handleAWT: " + t);
        t.printStackTrace();
        displayUserMessage(t);
    }

    protected void handle(Thread t, Throwable e) {
        SvarogLogger.getSharedInstance().error("uncaught exception [" + e + "] in thread [" + (t.getId()) + "/" + (t.getName()) + "]");
        e.printStackTrace();
        displayUserMessage(e);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        handle(t, e);
    }
}
