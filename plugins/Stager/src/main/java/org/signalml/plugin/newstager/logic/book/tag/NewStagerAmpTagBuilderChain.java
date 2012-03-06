package org.signalml.plugin.newstager.logic.book.tag;

import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagBuilderData;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;

public class NewStagerAmpTagBuilderChain extends NewStagerAtomTagBuilderChain {

	public NewStagerAmpTagBuilderChain(NewStagerBookAtomTagBuilderData data) {
		super(data);
	}

	@Override
	public boolean process(NewStagerBookSample sample) {
		if (this.data.helpers.deltaPrimaryHelper.convertToTagSamples(sample)
				.size() > 0) {
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

}
