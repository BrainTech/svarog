package org.signalml.plugin.newstager.logic.book.tag.helper;

import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagHelperData;

public class NewStagerCountingBuilderHelper extends
	NewStagerBookAtomAbstractTagHelper implements
	INewStagerBookAtomCoeffHelper {

	private INewStagerBookAtomFilter filter;
	private NewStagerBookSample sample;
	private double value;

	public NewStagerCountingBuilderHelper(NewStagerBookAtomTagHelperData data, INewStagerBookAtomFilter filter) {
		super(data);
		this.filter = filter;

		this.sample = null;
	}

	@Override
	public double convertToValue(NewStagerBookSample sample) {
		if (this.sample != sample) {
			this.calculateValue(sample);
		}
		return this.value;
	}

	private void calculateValue(NewStagerBookSample sample) {
		this.sample = sample;
		this.value = this.filter.filter(sample.atoms).size();
	}

}
