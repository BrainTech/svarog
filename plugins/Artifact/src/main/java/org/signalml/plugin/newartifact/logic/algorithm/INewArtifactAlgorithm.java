package org.signalml.plugin.newartifact.logic.algorithm;

public interface INewArtifactAlgorithm {
	public double [][] computeHead(NewArtifactAlgorithmData data);
	public double [][] compute(NewArtifactAlgorithmData data);
	public double [][] computeTail(NewArtifactAlgorithmData data);
}
