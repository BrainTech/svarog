package org.signalml.plugin.newstager.logic.mgr;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.signalml.method.MethodExecutionTracker;
import org.signalml.plugin.method.logic.IPluginComputationMgrStep;
import org.signalml.plugin.method.logic.IPluginComputationMgrStepTrackerProxy;
import org.signalml.plugin.method.logic.PluginComputationMgr;
import org.signalml.plugin.newstager.data.NewStagerResult;
import org.signalml.plugin.newstager.data.logic.NewStagerComputationProgressPhase;
import org.signalml.plugin.newstager.data.logic.NewStagerMgrData;
import org.signalml.plugin.newstager.data.logic.NewStagerMgrStepData;

public class NewStagerComputationMgr extends
	PluginComputationMgr<NewStagerMgrData, NewStagerResult> {

	protected class Tracker
		implements
		IPluginComputationMgrStepTrackerProxy<NewStagerComputationProgressPhase> {

		public Tracker(MethodExecutionTracker tracker) {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void advance(int step) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setProgressPhase(NewStagerComputationProgressPhase phase,
					     Object... arguments) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isRequestingAbort() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isInterrupted() {
			// TODO Auto-generated method stub
			return false;
		}

	}

	private List<IPluginComputationMgrStep> steps;

	@Override
	protected Collection<IPluginComputationMgrStep> prepareStepChain() {
		this.steps = new LinkedList<IPluginComputationMgrStep>();

		NewStagerMgrStepData data = new NewStagerMgrStepData(
			this.data.stagerData, this.data.constants,
			this.data.parameters, this.data.fixedParameters,
			new Tracker(tracker),
			this.getThreadFactory());

		this.steps.add(new NewStagerSignalStatsStep(data));
		this.steps.add(new NewStagerBookProcessStep(data));
		this.steps.add(new NewStagerTagWriteStep(data));

		return this.steps;
	}

	@Override
	protected NewStagerResult prepareComputationResult() {
		return null;
	}
}
