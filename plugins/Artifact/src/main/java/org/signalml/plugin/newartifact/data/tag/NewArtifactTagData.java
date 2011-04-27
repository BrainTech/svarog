package org.signalml.plugin.newartifact.data.tag;

import org.signalml.plugin.newartifact.data.NewArtifactConstants;
import org.signalml.plugin.newartifact.data.NewArtifactParameters;

public class NewArtifactTagData {
	public final double source[][];
	public final NewArtifactParameters parameters;
	public final NewArtifactConstants constants;
	public final int eegChannels[];
	public final int excludedChannels[];

	public NewArtifactTagData(final double source[][],
				  final NewArtifactConstants constants,
				  final NewArtifactParameters parameters,
				  final int eegChannels[],
				  final int excludedChannels[]) {
		this.source = source;
		this.constants = constants;
		this.parameters = parameters;
		this.eegChannels = eegChannels;
		this.excludedChannels = excludedChannels;

	}
}
