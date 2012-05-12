package org.signalml.plugin.newstager.data.tag;

import java.util.Map;

import org.signalml.plugin.newstager.logic.book.tag.NewStagerBookAtomTagCreator;
import org.signalml.plugin.newstager.logic.book.tag.helper.NewStagerBookAtomSampleHelperSet;

public class NewStagerBookAtomTagBuilderData {

	public final Map<String, Integer> channelMap;
	public final NewStagerBookAtomSampleHelperSet helpers;
	public final NewStagerBookAtomTagCreator tagCreator;

	public NewStagerBookAtomTagBuilderData(
		Map<String, Integer> channelMap,
		NewStagerBookAtomSampleHelperSet converters,
		NewStagerBookAtomTagCreator tagCreator) {
		this.channelMap = channelMap;
		this.helpers = converters;
		this.tagCreator = tagCreator;
	}
}
