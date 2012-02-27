package org.signalml.plugin.newartifact.logic.algorithm;

import org.signalml.plugin.newartifact.exception.NewArtifactPluginException;

public interface INewArtifactAlgorithm {
	public double [][] computeHead(NewArtifactAlgorithmData data) throws NewArtifactPluginException;
	public double [][] compute(NewArtifactAlgorithmData data) throws NewArtifactPluginException;
	public double [][] computeTail(NewArtifactAlgorithmData data) throws NewArtifactPluginException;
}
