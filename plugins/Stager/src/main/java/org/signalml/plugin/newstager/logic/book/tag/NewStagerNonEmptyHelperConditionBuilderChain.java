package org.signalml.plugin.newstager.logic.book.tag;

import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagBuilderData;
import org.signalml.plugin.newstager.logic.book.tag.helper.INewStagerBookAtomPrimaryTagHelper;

public class NewStagerNonEmptyHelperConditionBuilderChain extends NewStagerConditionTagBuilderChain {

	private INewStagerBookAtomPrimaryTagHelper helper;

	public NewStagerNonEmptyHelperConditionBuilderChain(NewStagerBookAtomTagBuilderData data,
			INewStagerBookAtomPrimaryTagHelper helper) {
		super(data);
		this.helper = helper;
	}

	@Override
	protected boolean isConditionMet(NewStagerBookSample sample) {
		return this.helper.convertToTagSamples(sample).size() > 0;
	}

}
