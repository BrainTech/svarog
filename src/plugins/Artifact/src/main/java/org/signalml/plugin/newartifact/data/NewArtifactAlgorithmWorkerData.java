package org.signalml.plugin.newartifact.data;


public class NewArtifactAlgorithmWorkerData {
	public final NewArtifactConstants constants;
	public final NewArtifactData artifactData;

	public NewArtifactAlgorithmWorkerData(NewArtifactData artifactData,
					      NewArtifactConstants constants) {
		this.artifactData = artifactData;
		this.constants = constants;
	}
}
