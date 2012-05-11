package org.signalml.plugin.newstager.logic.book.tag.helper;

import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagHelperData;

public class NewStagerMuscleTagBuilderHelper extends NewStagerBookAtomAbstractTagHelper implements
	INewStagerBookAtomCoeffHelper {

	public NewStagerMuscleTagBuilderHelper(
		NewStagerBookAtomTagHelperData data) {
		super(data);
	}

	@Override
	public double convertToValue(NewStagerBookSample sample) {
		try {
			return this.data.muscle[sample.offset];
		} catch (IndexOutOfBoundsException e) {
			return 0.0d;
		}
	}

}
