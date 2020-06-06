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
	private PluginCheckedThreadGroup threadGroup;

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

	protected PluginCheckedThreadGroup getThreadGroup() {
		if (this.threadGroup == null) {
			this.threadGroup = new PluginCheckedThreadGroup();
		}

		return this.threadGroup;
	}

	protected Result doCompute() throws ComputationException,
		PluginToolInterruptedException, PluginToolAbortException {

		this.stepResults = new HashMap<>();

		Collection<IPluginComputationMgrStep> steps = this.prepareStepChain();

		Map<IPluginComputationMgrStep, Integer> tickMap = new HashMap<>(
			steps.size());
		for (IPluginComputationMgrStep step : steps) {
			step.initialize();
			tickMap.put(step, step.getStepNumberEstimate());
		}

		this.initializeRun(tickMap);

		PluginComputationMgrStepResult stepResult = null;
		for (IPluginComputationMgrStep step : steps) {
			stepResult = step.run(stepResult);
			this.stepResults.put(step, stepResult);
		}

		return this.prepareComputationResult();
	}

	protected abstract Collection<IPluginComputationMgrStep> prepareStepChain();

	protected void initializeRun(
		Map<IPluginComputationMgrStep, Integer> stepTicks) {

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
