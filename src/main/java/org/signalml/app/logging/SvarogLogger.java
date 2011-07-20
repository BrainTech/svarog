package org.signalml.app.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Permission;

import org.signalml.app.SvarogApplication;
import org.signalml.app.view.ViewerConsolePane;
import org.signalml.app.view.ViewerElementManager;

/**
 * Svarog logger aware of multithreading.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogLogger {
    private static SvarogLogger instance = null;

    public static SvarogLogger getSharedInstance() {
        if (null == instance) {
            synchronized (SvarogLogger.class) {
                if (null == instance)
                    instance = new SvarogLogger();
            }
        }

        return instance;
    }

    private SvarogLogger() {
    }
    
    private synchronized void appendMsgToConsole(String msg) {
        SvarogApplication sa = SvarogApplication.getSharedInstance();
        if (null != sa) {
            ViewerElementManager mgr = sa.getViewerElementManager();
            if (null != mgr) {
                ViewerConsolePane console = mgr.getConsole();
                if (null != console)
                    console.addTextNL(msg);
            }
        }
    }

    /** The actual stderr printer (therefore synchronized). */
    private synchronized void printMsg(String prefix, String msg) {
        Thread t = Thread.currentThread();
        String logMsg = "[" + (t.getId()) + "/" + (t.getName()) + "] " + prefix + ": " + msg;
        System.err.println(logMsg);
        appendMsgToConsole(logMsg);
    }

    /** Provides an exception string containing the stack trace etc. */
    private String getExceptionMessage(Throwable t) {
        StringWriter ws = new StringWriter();
        PrintWriter wp = new PrintWriter(ws);
        t.printStackTrace(wp);
        return ws.toString();
    }
    
    /** Prints simple {@link java.lang.ThreadGroup} information using {@link #debug(String)}. */
    public void debugThreads() {
        StringBuilder buf = new StringBuilder();
        ThreadGroup g = Thread.currentThread().getThreadGroup();

        if (null != g) {
            buf.append("" + g);
            g = g.getParent();
        }

        while (null != g) {
            buf.append("; " + g);
            g = g.getParent();
        }

        debug("Thread groups (to root): " + buf.toString());
    }

    /** Used by {@link SvarogSecurityManager} to log security decisions. */
    public void permissionDenied(Thread t, Permission p) {
        permissionDenied(t, p, null);
    }

    /** Used by {@link SvarogSecurityManager} to log security decisions. */
    public void permissionDenied(Thread t, Permission p, SecurityException e) {
        String msg = "permission DENIED [" + (t.getId()) + "/" + (t.getName()) + "]: " + (p.getName()) + "/" + (p.getActions()) + "/" + p;
        debug((null == e) ? msg : (msg + "\n" + getExceptionMessage(e)));
    }

    /** Used by {@link SvarogSecurityManager} to log security decisions. */
    public void permissionGranted(Thread t, Permission p) {
        // debug("permission GRANTED [" + (t.getId()) + "/" + (t.getName()) + "]: " + (p.getName()) + "/" + (p.getActions()) + "/" + p);
    }

    public void debug(String s) {
        printMsg("[debug]", s);
    }
    public void warning(String s) {
        printMsg("[warning]", s);
    }
    public void error(String s) {
        printMsg("[error]", s);
    }

    public void debug(Throwable t) {
        debug(getExceptionMessage(t));
    }
    public void warning(Throwable t) {
        warning(getExceptionMessage(t));
    }
    public void error(Throwable t) {
        error(getExceptionMessage(t));
    }

}
