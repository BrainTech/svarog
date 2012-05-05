package org.signalml.plugin.method.logic;

public interface IPluginComputationMgrStepTrackerProxy<ComputationProgressPhase> {

	void advance(IPluginComputationMgrStep step, int tick);
	void setTickerLimit(IPluginComputationMgrStep step, int limit);
	void setProgressPhase(ComputationProgressPhase phase, Object... arguments);
	boolean isRequestingAbort();
	boolean isInterrupted();
}
