package org.signalml.app;

import com.getsentry.raven.Raven;
import java.awt.Window;
import org.apache.log4j.Logger;
import org.signalml.app.view.common.dialogs.errors.Dialogs;

/**
 * Svarog exception handler.
 *
 * It is a singleton.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
	protected static final Logger logger = Logger.getLogger(SvarogExceptionHandler.class);

	private static SvarogExceptionHandler instance = null;
	private static boolean installed = false;
	private volatile Raven raven = null;

	/**
	 * Installs SvarogExceptionHandler instance as default exception handler.
	 *
	 * @author Stanislaw Findeisen (Eisenbits)
	 * @see Thread#setDefaultUncaughtExceptionHandler(java.lang.Thread.UncaughtExceptionHandler)
	 */
	public static void install() {
		if (!installed) {
			synchronized (SvarogExceptionHandler.class) {
				if (!installed) {
					Thread.setDefaultUncaughtExceptionHandler(getSharedInstance());
					installed = true;
					logger.debug("SvarogExceptionHandler successfully installed!");
				}
			}
		}
	}

	/**
	 * Assign a Raven instance to this exception handler.
	 * All exceptions processed by this handler will be sent to Raven.
	 * If NULL is passed, no Raven instance will be assigned.
	 *
	 * @param raven  Raven client
	 */
	public void setRaven(Raven raven) {
		this.raven = raven;
	}

	/** Returns the shared instance. */
	public static SvarogExceptionHandler getSharedInstance() {
		if (instance == null) {
			synchronized (SvarogExceptionHandler.class) {
				if (instance == null)
					instance = new SvarogExceptionHandler();
			}
		}
		return instance;
	}

	private SvarogExceptionHandler() {
	}

	/**
	 * Report exception to Raven (if already configured)
	 * and display user message.
	 *
	 * @param t  exception
	 */
	private static void processException(Throwable t, Raven raven) {
		if (raven != null) {
			synchronized (raven) {
				// Raven is not guaranteed to be thread-safe
				raven.sendException(t);
			}
		}
		displayUserMessage(t);
	}

	private static void displayUserMessage(Throwable t) {
		Dialogs.showExceptionDialog((Window) null, t);
	}

	protected void handleAWT(Throwable t) {
		logger.error("AWT exception handler", t);
		displayUserMessage(t);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		logger.fatal("uncaught exception in thread [" + (t.getId()) + "/" + (t.getName()) + "]", e);
		processException(e, raven);
	}
}
