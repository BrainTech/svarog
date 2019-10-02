package org.signalml.plugin.method.logic;

import java.util.concurrent.ThreadFactory;



public class PluginComputationMgrStepData<Phase> {
	public final IPluginComputationMgrStepTrackerProxy<Phase> tracker;
	public final ThreadFactory threadFactory;

	public PluginComputationMgrStepData(IPluginComputationMgrStepTrackerProxy<Phase> tracker,
										ThreadFactory threadFactory) {
		this.tracker = tracker;
		this.threadFactory = threadFactory;
	}
}
