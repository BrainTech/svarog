package org.signalml.app.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.signalml.util.SynchronizedPrintStream;

/**
 * Svarog logger (STDERR only). It is thread-safe.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogLoggerStdErr {
    private static SvarogLoggerStdErr instance = null;
    
    /** A STDERR wrapper. This is intented to be a singleton (for logging purposes), however nothing will stop you from writing to STDERR... */
    private static SynchronizedPrintStream ssStdErr = null;

    /**
     * getInstance is provided for convenience, however this class is
     * not a singleton.
     */
    public static SvarogLoggerStdErr getInstance() {
        if (null == instance) {
            synchronized (SvarogLoggerStdErr.class) {
                if (null == instance)
                    instance = new SvarogLoggerStdErr();
            }
        }

        return instance;
    }

    public static SynchronizedPrintStream getSynchronizedStdErr() {
        if (null == ssStdErr) {
            synchronized (SvarogLoggerStdErr.class) {
                if (null == ssStdErr)
                    ssStdErr = new SynchronizedPrintStream(java.lang.System.err);
            }
        }

        return ssStdErr;        
    }
    
    protected SvarogLoggerStdErr() {
        super();
    }

    /**
     * Returns the concatenation of the 2 strings, with '\n' inserted
     * in between (if needed).
     */
    private String concatNL(String a, String b) {
        if (a.endsWith("\n") || b.startsWith("\n"))
            return (a + b);
        else
            return (a + "\n" + b);
    }
    
    /** The actual stderr printer. */
    protected String printMsg(String prefix, String msg) {
        Thread t = Thread.currentThread();
        String logMsg = "[" + (t.getId()) + "/" + (t.getName()) + "] " + prefix + ": " + msg;
        getSynchronizedStdErr().println(logMsg);
        return logMsg;
    }

    /** Provides an exception string containing the stack trace etc. */
    protected String getExceptionMessage(Throwable t) {
        StringWriter ws = new StringWriter();
        PrintWriter wp = new PrintWriter(ws);
        t.printStackTrace(wp);
        return ws.toString();
    }
    
    /** Prints simple {@link java.lang.ThreadGroup} hierarchy information using {@link #debug(String)}. */
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
    
    /** Prints simple {@link java.lang.ClassLoader} hierarchy information using {@link #debug(String)}. */
    public void debugCL() {
        StringBuilder buf = new StringBuilder();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        if (null != cl) {
            buf.append("" + cl);
            cl = cl.getParent();
        }

        while (null != cl) {
            buf.append("; " + cl);
            cl = cl.getParent();
        }

        debug("Class loaders (to root): " + buf.toString());
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

    public void debug(String s, Throwable t) {
        debug(concatNL(s, getExceptionMessage(t)));
    }
    public void warning(String s, Throwable t) {
        warning(concatNL(s, getExceptionMessage(t)));
    }
    public void error(String s, Throwable t) {
        error(concatNL(s, getExceptionMessage(t)));
    }
}
