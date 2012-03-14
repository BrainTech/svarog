package org.signalml.plugin.method.logic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.data.logic.PluginMgrData;
import org.signalml.plugin.exception.PluginToolAbortException;
import org.signalml.plugin.exception.PluginToolInterruptedException;

public abstract class PluginComputationMgr<Data extends PluginMgrData, Result> {
	protected static final Logger log = Logger.getLogger(PluginComputationMgr.class);

	protected class CheckedThreadGroup extends ThreadGroup {

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
			log.debug("uncaughtException: " + t + ", " + e);

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

	protected Data data;
	protected MethodExecutionTracker tracker;
	protected Map<IPluginComputationMgrStep, PluginComputationMgrStepResult> stepResults;

	private ThreadFactory threadFactory;
	private CheckedThreadGroup threadGroup;

	public Result compute(Data data, MethodExecutionTracker tracker)
	throws ComputationException {
		this.data = data;
		this.tracker = tracker;
		
		try {
			return this.doCompute();
		} catch (PluginToolAbortException e) {
			return null;
		} catch (PluginToolInterruptedException e) {
			this.handleInterrupt();
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

	protected CheckedThreadGroup getThreadGroup() {
		if (this.threadGroup == null) {
			this.threadGroup = new CheckedThreadGroup();
		}

		return this.threadGroup;
	}

	protected Result doCompute() throws ComputationException,
		PluginToolInterruptedException, PluginToolAbortException {

		this.stepResults = new HashMap<IPluginComputationMgrStep, PluginComputationMgrStepResult>();

		Collection<IPluginComputationMgrStep> steps = this.prepareStepChain();

		int ticks = 0;
		for (IPluginComputationMgrStep step : steps) {
			step.initialize();
			ticks += step.getStepNumberEstimate();
		}

		this.initializeRun(steps, ticks);

		PluginComputationMgrStepResult stepResult = null;
		for (IPluginComputationMgrStep step : steps) {
			stepResult = step.run(stepResult);
			this.stepResults.put(step, stepResult);
		}

		return this.prepareComputationResult();
	}

	protected abstract Collection<IPluginComputationMgrStep> prepareStepChain();

	protected void initializeRun(Collection<IPluginComputationMgrStep> steps, int ticks) {

	}

	protected abstract Result prepareComputationResult();

	private void handleInterrupt() throws ComputationException {
		if (this.threadGroup != null) {
			Throwable cause = this.threadGroup.getCause();
			if (cause != null) {
				log.error("Error in worker thread "
					  + this.threadGroup.getCausingThread().getId());
				throw new ComputationException(cause);
			}
		}
	}
}
