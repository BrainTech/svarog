package org.signalml.plugin.newstager.data.logic;

import java.util.concurrent.ThreadFactory;

import org.signalml.plugin.method.logic.IPluginComputationMgrStepTrackerProxy;
import org.signalml.plugin.method.logic.PluginComputationMgrStepData;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerData;
import org.signalml.plugin.newstager.data.NewStagerFixedParameters;
import org.signalml.plugin.newstager.data.NewStagerParameters;

public class NewStagerMgrStepData extends PluginComputationMgrStepData<NewStagerComputationProgressPhase> {

	public final NewStagerData stagerData;
	public final NewStagerConstants constants;
	public final NewStagerParameters parameters;
	public final NewStagerFixedParameters fixedParameters;

	public NewStagerMgrStepData(NewStagerData stagerData,
								NewStagerConstants constants,
								NewStagerParameters parameters,
								NewStagerFixedParameters fixedParameters,
								IPluginComputationMgrStepTrackerProxy<NewStagerComputationProgressPhase> tracker,
								ThreadFactory threadFactory) {
		super(tracker, threadFactory);
		this.stagerData = stagerData;
		this.constants = constants;
		this.parameters = parameters;
		this.fixedParameters = fixedParameters;
	}

}
