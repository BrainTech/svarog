package org.signalml.plugin.method.logic;

import org.signalml.method.ComputationException;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.exception.PluginToolAbortException;

public abstract class AbstractPluginComputationMgrStep<Data extends PluginComputationMgrStepData<?>>
	implements IPluginComputationMgrStep {

	protected final Data data;

	public AbstractPluginComputationMgrStep(Data data) {
		this.data = data;
	}

	@Override
	public PluginComputationMgrStepResult run(PluginComputationMgrStepResult prevStepResult) throws ComputationException, InterruptedException,
		PluginToolAbortException {
		try {
			return this.doRun(prevStepResult);
		} finally {
			this.cleanup();
		}
	}

	@Override
	public void initialize() throws ComputationException {
		try {
			this.doInitialize();
		} catch (Exception e) {
			this.cleanup();
			throw new ComputationException("Error in step initialization", e);
		}
	}

	protected abstract PluginComputationMgrStepResult prepareStepResult();

	protected void doInitialize() {

	}

	protected abstract PluginComputationMgrStepResult doRun(PluginComputationMgrStepResult prevStepResult) throws PluginToolAbortException, InterruptedException,
		ComputationException;

	protected void cleanup() {

	}

	protected void checkAbortState() throws PluginToolAbortException {
		if (this.data.tracker.isRequestingAbort()) {
			throw new PluginToolAbortException();
		}
	}

}
