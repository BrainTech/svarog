package org.signalml.plugin.newstager.logic.book.tag.helper;

import org.signalml.plugin.newstager.data.book.NewStagerAdaptedAtom;
import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagHelperData;

public class NewStagerKCTagBuilderHelper extends
	NewStagerBookAtomAbstractTagHelper implements
	INewStagerBookAtomCoeffHelper {

	private static final double THRESHOLD = 1.4d;

	private INewStagerBookAtomFilter kcGaborFilter;

	private NewStagerBookSample sample;

	private double result;

	public NewStagerKCTagBuilderHelper(NewStagerBookAtomTagHelperData data, INewStagerBookAtomFilter kcGaborFilter) {
		super(data);
		this.kcGaborFilter = kcGaborFilter;
		this.sample = null;
	}

	@Override
	public double convertToValue(NewStagerBookSample sample) {
		if (this.sample != sample) {
			this.sample = sample;
			this.computeResult(sample);
		}

		return this.result;
	}

	private void computeResult(NewStagerBookSample sample) {
		double mean = 0.0d;
		double ampli[] = {0d, 0d, 0d};
		int length = Math.min(3, sample.atoms.length);
		for (int i = 0; i < length; ++i) {
			double amplitude = sample.atoms[i].amplitude;
			mean += amplitude;
			ampli[i] = amplitude;
		}
		mean /= length;

		int count = 0;
		for (NewStagerAdaptedAtom atom : this.kcGaborFilter.filter(sample.atoms)) {
			boolean selected = true;
			for (int i = 0; i < 3; ++i) {
				if (atom.amplitude != ampli[i]) {
					selected = false;
					break;
				}
			}

			if (selected) {
				if (atom.amplitude > THRESHOLD * (mean - atom.amplitude / 3)) {
					++count;
				}
			} else {
				if (atom.amplitude > THRESHOLD * mean) {
					++count;
				}
			}

		}

		this.result = (double) count;
	}

}
