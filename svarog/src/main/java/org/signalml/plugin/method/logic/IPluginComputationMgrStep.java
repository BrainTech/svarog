package org.signalml.plugin.method.logic;

import org.signalml.method.ComputationException;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.exception.PluginToolAbortException;
import org.signalml.plugin.exception.PluginToolInterruptedException;

public interface IPluginComputationMgrStep {
	public int getStepNumberEstimate();

	public void initialize() throws ComputationException;

	public PluginComputationMgrStepResult run(PluginComputationMgrStepResult prevStepResult) throws ComputationException, PluginToolInterruptedException, PluginToolAbortException;
}
