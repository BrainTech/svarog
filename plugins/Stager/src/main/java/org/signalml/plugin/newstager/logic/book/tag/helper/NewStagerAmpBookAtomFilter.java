package org.signalml.plugin.newstager.logic.book.tag.helper;

import org.signalml.plugin.newstager.data.book.NewStagerAdaptedAtom;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomFilterData;
import org.signalml.plugin.newstager.logic.helper.NewStagerRangeGabor;
import org.signalml.util.MinMaxRange;

public class NewStagerAmpBookAtomFilter extends NewStagerBookAtomFilterBase {

	public NewStagerAmpBookAtomFilter(
			NewStagerBookAtomFilterData data) {
		super(data);

		MinMaxRange amplitude = data.threshold.amplitude;
		if (amplitude == null) {
			return;
		}

		final double samplingFrequency = data.bookInfo.samplingFrequency;
		final int signalSize = (int) (data.bookInfo.offsetDimension / samplingFrequency); //TODO maybe change to double?
		
		
		final INewStagerBookAtomSelector oldSelector = this.selector;
		final double min = amplitude.getMin();

		this.selector = new INewStagerBookAtomSelector() {

			@Override
			public boolean matches(NewStagerAdaptedAtom atom) {
				return oldSelector.matches(atom)
						&& NewStagerRangeGabor.HRangeGabor(signalSize,
								samplingFrequency, atom.scale, atom.frequency,
								atom.position, atom.amplitude, atom.phase) >= min;
			}
		};
	}

}
