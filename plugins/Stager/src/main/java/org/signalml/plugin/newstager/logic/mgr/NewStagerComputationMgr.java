package org.signalml.plugin.newstager.logic.mgr;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.signalml.method.MethodExecutionTracker;
import org.signalml.plugin.method.logic.IPluginComputationMgrStep;
import org.signalml.plugin.method.logic.IPluginComputationMgrStepTrackerProxy;
import org.signalml.plugin.method.logic.PluginComputationMgr;
import org.signalml.plugin.newstager.data.NewStagerResult;
import org.signalml.plugin.newstager.data.logic.NewStagerComputationProgressPhase;
import org.signalml.plugin.newstager.data.logic.NewStagerMgrData;
import org.signalml.plugin.newstager.data.logic.NewStagerMgrStepData;
import org.signalml.plugin.newstager.data.logic.NewStagerTagWriteStepResult;

public class NewStagerComputationMgr extends
		PluginComputationMgr<NewStagerMgrData, NewStagerResult> {

	protected class Tracker
			implements
			IPluginComputationMgrStepTrackerProxy<NewStagerComputationProgressPhase> {
		// TODO extract common base class

		final private CheckedThreadGroup threadGroup;
		final private MethodExecutionTracker tracker;
		final private List<IPluginComputationMgrStep> steps;

		public Tracker(CheckedThreadGroup threadGroup,
				MethodExecutionTracker tracker,
				List<IPluginComputationMgrStep> steps) {
			this.threadGroup = threadGroup;
			this.tracker = tracker;
			this.steps = steps;
		}

		@Override
		public void advance(IPluginComputationMgrStep step, int tick) {
			this.tracker.tick(0, tick);
		}

		@Override
		public void setProgressPhase(NewStagerComputationProgressPhase phase,
				Object... arguments) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isRequestingAbort() {
			return this.tracker.isRequestingAbort();
		}

		@Override
		public boolean isInterrupted() {
			return this.threadGroup.isShutdownStarted() || Thread.interrupted();
		}

	}

	private List<IPluginComputationMgrStep> steps;

	@Override
	protected Collection<IPluginComputationMgrStep> prepareStepChain() {
		this.steps = new LinkedList<IPluginComputationMgrStep>();

		NewStagerMgrStepData data = new NewStagerMgrStepData(
				this.data.stagerData, this.data.constants, new Tracker(
						this.getThreadGroup(), tracker, this.steps),
				this.getThreadFactory());

		this.steps.add(new NewStagerSignalStatsStep(data));
		this.steps.add(new NewStagerBookProcessStep(data));
		this.steps.add(new NewStagerTagWriteStep(data));

		return this.steps;
	}

	@Override
	protected void initializeRun(
			Map<IPluginComputationMgrStep, Integer> stepTicks) {
		final int values[] = {
				this.getTickEstimate(stepTicks, 0),
				this.getTickEstimate(stepTicks, 1)
						+ this.getTickEstimate(stepTicks, 2) };
		this.tracker.setTickerLimits(values);
	}

	@Override
	protected NewStagerResult prepareComputationResult() {
		if (this.steps.size() != 3) {
			return null;
		}
		
		NewStagerTagWriteStepResult castedWriterResult;
		try {
			castedWriterResult = (NewStagerTagWriteStepResult) this.stepResults.get(this.steps.get(2));
		} catch (ClassCastException e) {
			logger.error("Unexpected step result class");
			return null;
		}
	
		NewStagerResult result = new NewStagerResult();
		result.setTagFile(new File(castedWriterResult.primaryTagFilePath));
		return result;
	}
	
	private int getTickEstimate(Map<IPluginComputationMgrStep, Integer> stepTicks, int i) {
		if (this.steps.size() <= i) {
			return 0;
		}
		
		Integer stepTick = stepTicks.get(this.steps.get(i));
		return stepTick == null ? 0 : stepTick.intValue();
	}
}
