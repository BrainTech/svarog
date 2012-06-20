package org.signalml.app;

/**
 * A Svarog thread group.
 *
 * It is very good at catching Svarog exceptions. :-)
 *
 * This is a singleton.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 *
 */
public class SvarogThreadGroup extends java.lang.ThreadGroup {
	private static SvarogThreadGroup instance = null;

	/** Returns the shared instance (initializes it first, if needed). */
	public static SvarogThreadGroup getSharedInstance() {
		if (instance == null) {
			synchronized (SvarogThreadGroup.class) {
				if (instance == null)
					instance = new SvarogThreadGroup();
			}
		}
		return instance;
	}

	private SvarogThreadGroup() {
		super("SvarogThreadGroup");
	}

	/**
	 * Thread uncaught exception handler. This method simply calls
	 * {@link SvarogExceptionHandler#uncaughtException(Thread, Throwable)}.
	 */
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		SvarogExceptionHandler.getSharedInstance().uncaughtException(t, e);
	}

	/** Creates a new thread in this group. */
	public Thread createNewThread(Runnable r) {
		return new Thread(this, r);
	}

	/** Creates a new thread in this group. */
	public Thread createNewThread(Runnable r, String name) {
		return new Thread(this, r, name);
	}
}
