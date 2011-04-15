package org.signalml.plugin.method.logic;

import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.data.logic.PluginMgrData;
import org.signalml.plugin.exception.PluginToolAbortException;

public abstract class PluginComputationMgr<Data extends PluginMgrData, Result extends PluginComputationMgrStepResult> {

	public Result compute(Data data, MethodExecutionTracker tracker)
	throws ComputationException {
		try {
			return this.doCompute(data, tracker);
		} catch (PluginToolAbortException e) {
			return null;
		} finally {
			tracker.setMessage(null);
		}
	}

	protected abstract Result doCompute(Data data,
					    MethodExecutionTracker tracker) throws ComputationException, PluginToolAbortException; //TODO ComputationException
}
