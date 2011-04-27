package org.signalml.plugin.newartifact.data.mgr;

import org.signalml.plugin.data.logic.PluginMgrData;
import org.signalml.plugin.newartifact.data.NewArtifactConstants;
import org.signalml.plugin.newartifact.data.NewArtifactData;


public class NewArtifactMgrData extends PluginMgrData {
	public NewArtifactData artifactData;
	public NewArtifactConstants constants;


	public NewArtifactMgrData(NewArtifactData artifactData, NewArtifactConstants constants) {
		this.artifactData = artifactData;
		this.constants = constants;
	}
}
