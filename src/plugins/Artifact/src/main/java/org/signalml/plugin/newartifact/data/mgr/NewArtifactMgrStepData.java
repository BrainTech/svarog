package org.signalml.plugin.newartifact.data.mgr;

import org.signalml.plugin.method.logic.IPluginComputationMgrStepTrackerProxy;
import org.signalml.plugin.method.logic.PluginComputationMgrStepData;
import org.signalml.plugin.newartifact.data.NewArtifactConstants;
import org.signalml.plugin.newartifact.data.NewArtifactData;
import org.signalml.plugin.newartifact.logic.mgr.INewArtifactPathConstructor;
import org.signalml.plugin.newartifact.logic.mgr.NewArtifactProgressPhase;

public class NewArtifactMgrStepData extends PluginComputationMgrStepData<NewArtifactProgressPhase> {
	public final NewArtifactData artifactData;
	public final NewArtifactConstants constants;
	public final INewArtifactPathConstructor pathConstructor;

	public NewArtifactMgrStepData(final NewArtifactData artifactData,
				      final NewArtifactConstants constants,
				      final INewArtifactPathConstructor pathConstructor,
				      final IPluginComputationMgrStepTrackerProxy<NewArtifactProgressPhase> tracker) {
		super(tracker);
		this.artifactData = artifactData;
		this.constants = constants;
		this.pathConstructor = pathConstructor;
	}
}
