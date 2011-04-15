package org.signalml.plugin.method.logic;



public class PluginComputationMgrStepData<Phase> {
	public final IPluginComputationMgrStepTrackerProxy<Phase> tracker;

	public PluginComputationMgrStepData(IPluginComputationMgrStepTrackerProxy<Phase> tracker) {
		this.tracker = tracker;
	}
}
