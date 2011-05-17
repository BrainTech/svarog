package org.signalml.plugin.method.logic;

import org.signalml.method.ComputationException;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.exception.PluginToolAbortException;

public interface IPluginComputationMgrStep {
	public int getStepNumberEstimate();

	public void initialize() throws ComputationException;

	public PluginComputationMgrStepResult run(PluginComputationMgrStepResult prevStepResult) throws ComputationException, InterruptedException, PluginToolAbortException;
}
