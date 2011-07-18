package org.signalml.app;

import java.awt.AWTEvent;

/**
 * Svarog AWT event queue.
 * 
 * It is very good at catching exceptions thrown on AWT event dispatch thread
 * (for instance by plugins). :-)
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogAWTEventQueue extends java.awt.EventQueue {
    
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
        } catch (Throwable t) {
            SvarogExceptionHandler.getSharedInstance().handleAWT(t);
        }
    }
}
