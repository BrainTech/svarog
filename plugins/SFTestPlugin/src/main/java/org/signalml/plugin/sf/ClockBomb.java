package org.signalml.plugin.sf;

import org.apache.log4j.Logger;

/**
 * java.lang.Runnable that throws RuntimeException after specified time
 * has passed.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
class ClockBomb extends Timer implements java.lang.Runnable {
    protected static final Logger log = Logger.getLogger(ClockBomb.class);

    public ClockBomb() {
        this(0);
    }

    public ClockBomb(int millis) {
        super(millis);
    }

    @Override
    public void run() {
        super.run();
        
        log.debug("ClockBomb: explode");
        log.debug("ClockBomb: getDefaultUncaughtExceptionHandler: " + Thread.getDefaultUncaughtExceptionHandler());
        throw new RuntimeException("ClockBomb explode (" + getMillis() + " millis)");
        // throw new OutOfMemoryError("ClockBomb explode (" + getMillis() + " millis)");
    }
}
