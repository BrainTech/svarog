package org.signalml.app;

import java.awt.AWTEvent;

import org.apache.log4j.Logger;

/**
 * Svarog AWT event queue.
 * 
 * It is very good at catching exceptions thrown on AWT event dispatch thread
 * (for instance by plugins). :-)
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogAWTEventQueue extends java.awt.EventQueue {
    protected static final Logger log = Logger.getLogger(SvarogAWTEventQueue.class);
    
    private static boolean queueReplaced = false;
    
    /** Replaces the default AWT event queue by a new instance of this one. */
    public static void install() {
        if (! queueReplaced) {
            synchronized (SvarogAWTEventQueue.class) {
                if (! queueReplaced) {
                    java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue().push(new SvarogAWTEventQueue());
                    queueReplaced = true;
                }
            }
        }
    }

    private SvarogAWTEventQueue() {
        super();
    }

    @Override
    /**
     * Calls {@link java.awt.EventQueue#dispatchEvent} and catches any
     * {@link Throwable}s. If any is caught,
     * {@link SvarogExceptionHandler#handleAWT(Throwable)} is called.
     * 
     * @author Stanislaw Findeisen (Eisenbits)
     */
    protected void dispatchEvent(AWTEvent newEvent) {
        try {
            super.dispatchEvent(newEvent);
        } catch (Error t) {
            try {
                SvarogExceptionHandler.getSharedInstance().handleAWT(t);
            } catch (RuntimeException e) {
                log.error("Error in exception handler!", e);
            }

            throw t;
        } catch (Exception t) {
            SvarogExceptionHandler.getSharedInstance().handleAWT(t);
        }
    }
}
