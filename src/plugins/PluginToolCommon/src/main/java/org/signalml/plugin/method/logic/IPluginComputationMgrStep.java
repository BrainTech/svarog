package org.signalml.plugin.method.logic;

import org.signalml.method.ComputationException;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.exception.PluginToolAbortException;

public interface IPluginComputationMgrStep<Result extends PluginComputationMgrStepResult> {
	public int getStepNumberEstimate();

	public Result run() throws ComputationException, InterruptedException, PluginToolAbortException;
}
