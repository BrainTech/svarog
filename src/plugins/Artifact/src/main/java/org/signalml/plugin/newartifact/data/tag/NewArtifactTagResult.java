package org.signalml.plugin.newartifact.data.tag;

import java.util.SortedSet;

public class NewArtifactTagResult {

	public final String name;
	public final SortedSet<Integer> sortedTags;
	public final int tagStretchFactor;
	public final String description;

	public NewArtifactTagResult(String name, SortedSet<Integer> sortedTags,
				    int tagStretchFactor,
				    String description) {
		this.sortedTags = sortedTags;
		this.name = name;
		this.tagStretchFactor = tagStretchFactor;
		this.description = description;
	}

}
