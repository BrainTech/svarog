package org.signalml.plugin.newstager.logic.book.tag;

import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagBuilderData;
import org.signalml.plugin.newstager.logic.book.tag.helper.INewStagerBookAtomCoeffHelper;

public class NewStagerHelperValueThresholdTagBuilderChain extends NewStagerConditionTagBuilderChain {

	private double threshold;
	private INewStagerBookAtomCoeffHelper helper;
	
	public NewStagerHelperValueThresholdTagBuilderChain(
			NewStagerBookAtomTagBuilderData data,
			INewStagerBookAtomCoeffHelper helper,
			double threshold) {
		super(data);
		this.helper = helper;
		this.threshold = threshold;
	}

	@Override
	protected boolean isConditionMet(NewStagerBookSample sample) {
		return this.helper.convertToValue(sample) > this.threshold;
	}
	
}
