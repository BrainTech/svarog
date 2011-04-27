package org.signalml.plugin.newartifact.data.algorithm;


public class NewArtifactAlgorithmResult implements INewArtifactAlgorithmResult {
	private final double data[][];

	NewArtifactAlgorithmResult(double data[][]) {
		this.data = data;
	}

	@Override
	public double[][] getResultData() {
		return this.data;
	}

}
