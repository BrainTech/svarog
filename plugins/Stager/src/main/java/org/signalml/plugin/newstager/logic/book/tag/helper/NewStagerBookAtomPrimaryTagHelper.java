package org.signalml.plugin.newstager.logic.book.tag.helper;

import java.util.Collection;

import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagHelperData;
import org.signalml.plugin.newstager.data.tag.NewStagerHelperTagSample;

public class NewStagerBookAtomPrimaryTagHelper extends NewStagerBookAtomAbstractTagComputingHelper implements
	INewStagerBookAtomPrimaryTagHelper {

	private INewStagerBookAtomFilter filter;
	private Collection<NewStagerHelperTagSample> tags;
	private NewStagerBookSample sample;

	public NewStagerBookAtomPrimaryTagHelper(NewStagerBookAtomTagHelperData data, INewStagerBookAtomFilter filter) {
		super(data);
		this.filter = filter;
		this.tags = null;
		this.sample = null;
	}

	@Override
	public Collection<NewStagerHelperTagSample> convertToTagSamples(NewStagerBookSample sample) {
		if (this.sample != sample) {
			this.sample = sample;
			this.tags = this.computeTags(sample.offset, filter.filter(sample.atoms));
		}
		return this.tags;
	}

}
