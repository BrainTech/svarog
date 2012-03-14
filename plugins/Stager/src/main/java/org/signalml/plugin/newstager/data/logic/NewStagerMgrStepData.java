package org.signalml.plugin.newstager.data.logic;

import java.util.concurrent.ThreadFactory;

import org.signalml.plugin.method.logic.IPluginComputationMgrStepTrackerProxy;
import org.signalml.plugin.method.logic.PluginComputationMgrStepData;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerData;

public class NewStagerMgrStepData extends PluginComputationMgrStepData<NewStagerComputationProgressPhase> {

	public final NewStagerData stagerData;
	public final NewStagerConstants constants;

	public NewStagerMgrStepData(NewStagerData stagerData,
			NewStagerConstants constants,
			IPluginComputationMgrStepTrackerProxy<NewStagerComputationProgressPhase> tracker,
			ThreadFactory threadFactory) {
		super(tracker, threadFactory);
		this.stagerData = stagerData;
		this.constants = constants;
	}

}
