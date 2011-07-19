package org.signalml.plugin.sf;

import org.signalml.app.logging.SvarogLogger;

/**
 * java.lang.Runnable that throws RuntimeException after specified time
 * has passed.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
class ClockBomb implements java.lang.Runnable {
    private int millis;
    
    public ClockBomb() {
        this(0);
    }

    public ClockBomb(int millis) {
        this.millis = millis;
    }

    @Override
    public void run() {
        SvarogLogger sl = SvarogLogger.getSharedInstance();

        try {
            sl.debug("ClockBomb: sleep " + millis);
            Thread.sleep(millis);
            sl.debug("ClockBomb: explode");
            sl.debug("Thread.getDefaultUncaughtExceptionHandler: " + Thread.getDefaultUncaughtExceptionHandler());
        } catch (InterruptedException e) {
            sl.debug("ClockBomb: interrupted: " + e);
            throw new RuntimeException(e);
        }

        throw new RuntimeException("ClockBomb explode (" + millis + " millis)");
    }
}
