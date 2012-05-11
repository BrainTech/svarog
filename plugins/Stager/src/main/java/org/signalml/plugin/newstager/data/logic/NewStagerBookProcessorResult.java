package org.signalml.plugin.newstager.data.logic;

import java.util.Map;

import org.signalml.plugin.newstager.data.NewStagerBookInfo;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollection;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;

public class NewStagerBookProcessorResult {

	public Map<NewStagerTagCollectionType, NewStagerTagCollection> tagCollectionMap;

	public NewStagerBookProcessorResult(
		NewStagerBookInfo bookInfo,
		Map<NewStagerTagCollectionType, NewStagerTagCollection> tagCollectionMap) {
		this.tagCollectionMap = tagCollectionMap;
	}

}
