package org.signalml.plugin.sf;

import org.apache.log4j.Logger;

/**
 *
 * @author Stanislaw Findeisen (Eisenbits)
 *
 */
class Timer implements java.lang.Runnable {
	protected static final Logger log = Logger.getLogger(Timer.class);

	private int millis;

	protected Timer() {
		this(0);
	}

	protected Timer(int millis) {
		this.millis = millis;
	}

	@Override
	public void run() {
		try {
			log.debug("sleep " + millis);
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			log.debug("interrupted: " + e);
			throw new RuntimeException(e);
		}
	}

	public int getMillis() {
		return millis;
	}
}
