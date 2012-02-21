package org.signalml.plugin.newartifact.logic.algorithm;

public interface INewArtifactAlgorithm {
	public double [][] computeHead(NewArtifactAlgorithmData data) throws NewArtifactAlgorithmDataException;
	public double [][] compute(NewArtifactAlgorithmData data) throws NewArtifactAlgorithmDataException;
	public double [][] computeTail(NewArtifactAlgorithmData data) throws NewArtifactAlgorithmDataException;
}
