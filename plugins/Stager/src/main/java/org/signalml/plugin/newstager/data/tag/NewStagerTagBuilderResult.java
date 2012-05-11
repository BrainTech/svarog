package org.signalml.plugin.newstager.data.tag;

import java.util.Map;

public class NewStagerTagBuilderResult {

	public final Map<NewStagerTagCollectionType, NewStagerTagCollection> tagMap;
	public final boolean freshResult;

	public NewStagerTagBuilderResult(Map<NewStagerTagCollectionType, NewStagerTagCollection> tagMap, boolean freshResult) {
		this.tagMap = tagMap;
		this.freshResult = freshResult;
	}
}
