package org.signalml.plugin.newartifact.data.tag;

import org.signalml.plugin.newartifact.data.NewArtifactConstants;
import org.signalml.plugin.newartifact.data.NewArtifactParameters;


public class NewArtifactTagRoutineData {
	public final NewArtifactConstants constants;
	public final NewArtifactParameters parameters;
	public final int eegChannels[];
	public final int excludedChannels[];

	public NewArtifactTagRoutineData(final NewArtifactConstants constants,
									 final NewArtifactParameters parameters,
									 final int eegChannels[],
									 final int excludedChannels[]) {
		this.constants = constants;
		this.parameters = parameters;
		this.eegChannels = eegChannels;
		this.excludedChannels = excludedChannels;

	}
}
