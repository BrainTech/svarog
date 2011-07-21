package org.signalml.plugin.sf;

import org.signalml.app.logging.SvarogLogger;

/**
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 *
 */
class Timer implements java.lang.Runnable {
    private int millis;
    
    protected Timer() {
        this(0);
    }

    protected Timer(int millis) {
        this.millis = millis;
    }

    @Override
    public void run() {
        SvarogLogger sl = SvarogLogger.getSharedInstance();

        try {
            sl.debug("Timer: sleep " + millis);
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            sl.debug("Timer: interrupted: " + e);
            throw new RuntimeException(e);
        }
    }
    
    public int getMillis() {
        return millis;
    }
}
