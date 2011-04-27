package org.signalml.plugin.newartifact.logic.mgr;

import org.signalml.plugin.newartifact.data.NewArtifactComputationType;

public interface INewArtifactPathConstructor {
	public String[] getIntermediateFileNamesForAlgorithm(
		NewArtifactComputationType algorithmType);

	public String getPathToWorkDir();

	public String getTagFileExtension();
}
