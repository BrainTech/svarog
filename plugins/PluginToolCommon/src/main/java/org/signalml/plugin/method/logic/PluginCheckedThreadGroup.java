package org.signalml.plugin.method.logic;

import org.apache.log4j.Logger;

public class PluginCheckedThreadGroup extends ThreadGroup {
	protected static final Logger logger = Logger
			.getLogger(PluginCheckedThreadGroup.class);
	
	private Thread parentThread;

	private boolean isShutdownStarted;
	private Throwable cause;
	private Thread causingThread;

	public PluginCheckedThreadGroup() {
		super(Thread.currentThread().getThreadGroup(),
				"PluginComputationGroup");

		this.parentThread = Thread.currentThread();

		this.isShutdownStarted = false;
		this.cause = null;
		this.causingThread = null;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		logger.debug("uncaughtException: " + t + ", " + e);

		synchronized (this) {
			if (this.isShutdownStarted) {
				super.uncaughtException(t, e);
			} else {
				this.isShutdownStarted = true;
				this.cause = e;
				this.causingThread = t;

				this.parentThread.interrupt();
			}

		}
	}

	public boolean isShutdownStarted() {
		return this.isShutdownStarted;
	}

	public Throwable getCause() {
		synchronized (this) {
			return this.cause;
		}
	}

	public Thread getCausingThread() {
		synchronized (this) {
			return this.causingThread;
		}
	}
}
