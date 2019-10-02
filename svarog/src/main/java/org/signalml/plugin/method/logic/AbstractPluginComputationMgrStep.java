package org.signalml.plugin.method.logic;

import org.signalml.method.ComputationException;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.exception.PluginToolAbortException;
import org.signalml.plugin.exception.PluginToolInterruptedException;
import static org.signalml.app.util.i18n.SvarogI18n._;

public abstract class AbstractPluginComputationMgrStep<Data extends PluginComputationMgrStepData<?>>
	implements IPluginComputationMgrStep {

	protected final Data data;

	public AbstractPluginComputationMgrStep(Data data) {
		this.data = data;
	}

	@Override
	public PluginComputationMgrStepResult run(
		PluginComputationMgrStepResult prevStepResult)
	throws ComputationException, PluginToolInterruptedException,
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
			throw new ComputationException(_("Error in step initialization"), e);
		}
	}

	protected abstract PluginComputationMgrStepResult prepareStepResult();

	protected void doInitialize() {

	}

	protected abstract PluginComputationMgrStepResult doRun(
		PluginComputationMgrStepResult prevStepResult)
	throws PluginToolAbortException, PluginToolInterruptedException,
		ComputationException;

	protected void cleanup() {

	}

	protected void checkAbortState() throws PluginToolAbortException,
		PluginToolInterruptedException {
		if (this.data.tracker.isRequestingAbort()) {
			throw new PluginToolAbortException();
		}
		if (this.data.tracker.isInterrupted()) {
			throw new PluginToolInterruptedException();
		}
	}

}
