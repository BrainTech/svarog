package org.signalml.plugin.newstager.logic.book.tag;

import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagBuilderData;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;

public abstract class NewStagerConditionTagBuilderChain extends NewStagerAtomTagBuilderChain {

	public NewStagerConditionTagBuilderChain(
		NewStagerBookAtomTagBuilderData data) {
		super(data);
	}

	@Override
	public boolean process(NewStagerBookSample sample) {
		if (this.isConditionMet(sample)) {
			super.process(sample);
			return true;
		}
		return false;
	}

	@Override
	protected NewStagerTagCollectionType getTagType() {
		// TODO Auto-generated method stub
		return null;
	}

	protected abstract boolean isConditionMet(NewStagerBookSample sample);
}
