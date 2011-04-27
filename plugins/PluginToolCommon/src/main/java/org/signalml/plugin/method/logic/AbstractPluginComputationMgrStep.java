package org.signalml.plugin.method.logic;

import org.signalml.method.ComputationException;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.exception.PluginToolAbortException;

public abstract class AbstractPluginComputationMgrStep<Data extends PluginComputationMgrStepData<?>, Result extends PluginComputationMgrStepResult>
	implements IPluginComputationMgrStep<Result> {

	protected final Data data;

	public AbstractPluginComputationMgrStep(Data data) {
		this.data = data;
	}

	@Override
	public Result run() throws ComputationException, InterruptedException,
		PluginToolAbortException {
		try {
			return this.doRun();
		} finally {
			this.cleanup();
		}
	}

	protected abstract Result prepareStepResult();

	protected abstract Result doRun() throws PluginToolAbortException, InterruptedException,
		ComputationException;

	protected void cleanup() {

	}

	protected void checkAbortState() throws PluginToolAbortException {
		if (this.data.tracker.isRequestingAbort()) {
			throw new PluginToolAbortException();
		}
	}

}
