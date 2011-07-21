package org.signalml.plugin.sf;

import org.signalml.app.logging.SvarogLogger;

/**
 * java.lang.Runnable that throws RuntimeException after specified time
 * has passed.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
class ClockBomb extends Timer implements java.lang.Runnable {
    public ClockBomb() {
        this(0);
    }

    public ClockBomb(int millis) {
        super(millis);
    }

    @Override
    public void run() {
        super.run();
        
        SvarogLogger sl = SvarogLogger.getSharedInstance();
        sl.debug("ClockBomb: explode");
        sl.debug("Thread.getDefaultUncaughtExceptionHandler: " + Thread.getDefaultUncaughtExceptionHandler());
        throw new RuntimeException("ClockBomb explode (" + getMillis() + " millis)");
    }
}
