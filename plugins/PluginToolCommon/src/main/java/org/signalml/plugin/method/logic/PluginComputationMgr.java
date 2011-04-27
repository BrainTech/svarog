package org.signalml.plugin.method.logic;

import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.data.logic.PluginMgrData;
import org.signalml.plugin.exception.PluginToolAbortException;

public abstract class PluginComputationMgr<Data extends PluginMgrData, Result extends PluginComputationMgrStepResult> {

	protected static final Logger logger = Logger
					       .getLogger(PluginComputationMgr.class);

	private class CheckedThreadGroup extends ThreadGroup {

		private Thread parentThread;

		private boolean isShutdownStarted;
		private Throwable cause;
		private Thread causingThread;

		public CheckedThreadGroup() {
			super(Thread.currentThread().getThreadGroup(),
			      "PluginComputationGroup");

			this.parentThread = Thread.currentThread();

			this.isShutdownStarted = false;
			this.cause = null;
			this.causingThread = null;
		}

		@Override
		public void uncaughtException(Thread t, Throwable e) {
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

	private class CheckedThreadFactory implements ThreadFactory {

		private final ThreadGroup group;

		public CheckedThreadFactory(ThreadGroup group) {
			this.group = group;
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(this.group, r);
			thread.setDaemon(true);
			return thread;
		}

	}

	private ThreadFactory threadFactory;
	private CheckedThreadGroup threadGroup;

	public Result compute(Data data, MethodExecutionTracker tracker)
	throws ComputationException {
		try {
			return this.doCompute(data, tracker);
		} catch (PluginToolAbortException e) {
			return null;
		} catch (InterruptedException e) {
			if (this.threadGroup != null) {
				Throwable cause = this.threadGroup.getCause();
				if (cause != null) {
					logger.error("Error in worker thread " + this.threadGroup.getCausingThread().getId());
					throw new ComputationException(cause);
				}
			}
			return null;
		} finally {
			tracker.setMessage(null);
		}
	}

	protected ThreadFactory getThreadFactory() {
		if (this.threadFactory == null) {
			this.threadFactory = new CheckedThreadFactory(this.getThreadGroup());
		}

		return this.threadFactory;
	}

	protected ThreadGroup getThreadGroup() {
		if (this.threadGroup == null) {
			this.threadGroup = new CheckedThreadGroup();
		}

		return this.threadGroup;
	}

	protected abstract Result doCompute(Data data,
					    MethodExecutionTracker tracker) throws ComputationException,
		InterruptedException, PluginToolAbortException; // TODO

}
