package org.signalml.plugin.method.logic;

public interface IPluginComputationMgrStepTrackerProxy<ComputationProgressPhase> {

	void advance(IPluginComputationMgrStep step, int tick);
	void setProgressPhase(ComputationProgressPhase phase, Object... arguments);
	boolean isRequestingAbort();
	boolean isInterrupted();
}
