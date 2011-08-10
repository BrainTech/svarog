package org.signalml.plugin.method.logic;

public interface IPluginComputationMgrStepTrackerProxy<ComputationProgressPhase> {

	void advance(int step);
	void setProgressPhase(ComputationProgressPhase phase, Object... arguments);
	boolean isRequestingAbort();
	boolean isInterrupted();
}
