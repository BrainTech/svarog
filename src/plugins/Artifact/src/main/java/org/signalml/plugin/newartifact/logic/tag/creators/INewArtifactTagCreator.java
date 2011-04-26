package org.signalml.plugin.newartifact.logic.tag.creators;

import org.signalml.plugin.newartifact.data.tag.NewArtifactTagData;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;

public interface INewArtifactTagCreator {
	public NewArtifactTagResult tag(NewArtifactTagData data);
}
