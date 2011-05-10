package org.signalml.plugin.newartifact.data.mgr;

import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.method.logic.AbstractPluginComputationMgrStep;

public class NewArtifactMgrStepResult extends PluginComputationMgrStepResult {
	public final Class<? extends AbstractPluginComputationMgrStep<?>> stepClass;
	public String resultTagPath;

	public NewArtifactMgrStepResult(
		Class<? extends AbstractPluginComputationMgrStep<?>> stepClass) {
		this.stepClass = stepClass;
	}
}
