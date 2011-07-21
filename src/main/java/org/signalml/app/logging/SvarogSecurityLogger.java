package org.signalml.app.logging;

import java.security.Permission;

/**
 * The logger to be used with {@link SvarogSecurityManager}.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogSecurityLogger extends SvarogLoggerStdErr {
    private static SvarogSecurityLogger instance = null;

    public static SvarogSecurityLogger getInstance() {
        if (null == instance) {
            synchronized (SvarogSecurityLogger.class) {
                if (null == instance)
                    instance = new SvarogSecurityLogger();
            }
        }

        return instance;
    }

    private SvarogSecurityLogger() {
        super();
    }

    /** Used by {@link SvarogSecurityManager} to log security decisions. */
    public void permissionDenied(Thread t, Permission p, SecurityException e, StackTraceElement frame) {
        String msg = "Permission DENIED [" + (t.getId()) + "/" + (t.getName()) + "]: " + p + "; plugin ctx: " + frame;
        warning((null == e) ? msg : (msg + "\n" + getExceptionMessage(e)));
    }

    /** Used by {@link SvarogSecurityManager} to log security decisions. */
    public void permissionGranted(Thread t, Permission p) {
        debug("Permission GRANTED [" + (t.getId()) + "/" + (t.getName()) + "]: " + p);
    }
}
