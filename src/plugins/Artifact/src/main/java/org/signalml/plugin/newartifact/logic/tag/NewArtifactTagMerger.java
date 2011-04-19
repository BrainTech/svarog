package org.signalml.plugin.newartifact.logic.tag;

import java.util.Collection;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;

public class NewArtifactTagMerger {

	private final static int STRETCH_FACTOR = 4;
	private final static String NAME = "a";
	private final static String DESCRIPTION = "artefakt <- Artefakt";

	private Collection<NewArtifactTagResult> tags;

	public NewArtifactTagMerger() {
		this.tags = new LinkedList<NewArtifactTagResult>();
	}

	public void addTag(NewArtifactTagResult tagResult) {
		tags.add(tagResult);
	}

	public NewArtifactTagResult merge() {
		SortedSet<Integer> sortedTags = new TreeSet<Integer>();
		for (NewArtifactTagResult tag : this.tags) {
			sortedTags.addAll(tag.sortedTags);
		}

		return new NewArtifactTagResult(NewArtifactTagMerger.NAME,
						sortedTags, NewArtifactTagMerger.STRETCH_FACTOR,
						NewArtifactTagMerger.DESCRIPTION);
	}
}
