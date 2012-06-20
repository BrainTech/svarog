package org.signalml.plugin.newartifact.logic.algorithm;

import java.util.Map;

import org.signalml.plugin.newartifact.data.NewArtifactConstants;
import org.signalml.plugin.newartifact.data.NewArtifactParameters;

public class NewArtifactAlgorithmData {
	public NewArtifactConstants constants;
	public NewArtifactParameters parameters;
	public Map<String, Integer> channels;
	public double signal[][];

	public NewArtifactAlgorithmData(NewArtifactConstants constants,
									NewArtifactParameters parameters, Map<String, Integer> channels,
									double signal[][]) {
		this.constants = constants;
		this.parameters = parameters;
		this.channels = channels;
		this.signal = signal;
	}
}
