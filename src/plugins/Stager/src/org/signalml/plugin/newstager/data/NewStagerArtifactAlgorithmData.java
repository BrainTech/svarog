package org.signalml.plugin.newstager.data;

import java.util.Map;

public class NewStagerArtifactAlgorithmData {
	public final Map<String, Integer> channels;
	public final NewStagerConstants constants;
	public final NewStagerParameters parameters;

	public NewStagerArtifactAlgorithmData(
		final Map<String, Integer> channels,
		final NewStagerConstants constants,
		final NewStagerParameters parameters) {
		this.channels = channels;
		this.constants = constants;
		this.parameters = parameters;
	}
}
