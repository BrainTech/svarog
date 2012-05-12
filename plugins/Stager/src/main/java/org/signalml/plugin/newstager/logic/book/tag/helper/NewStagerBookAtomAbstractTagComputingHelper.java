package org.signalml.plugin.newstager.logic.book.tag.helper;

import java.util.Collection;
import java.util.LinkedList;

import org.signalml.plugin.newstager.data.book.NewStagerAdaptedAtom;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagHelperData;
import org.signalml.plugin.newstager.data.tag.NewStagerHelperTagSample;

public abstract class NewStagerBookAtomAbstractTagComputingHelper extends
	NewStagerBookAtomAbstractTagHelper {

	protected double widthCoeff;

	private double samplingFrequency;
	private double offsetDimension;

	protected NewStagerBookAtomAbstractTagComputingHelper(
		NewStagerBookAtomTagHelperData data) {
		super(data);
		this.widthCoeff = 1.0;
		this.offsetDimension = this.data.bookInfo.offsetDimension;
		this.samplingFrequency = this.data.bookInfo.samplingFrequency;
	}

	protected Collection<NewStagerHelperTagSample> computeTags(int offset,
			Collection<NewStagerAdaptedAtom> atoms) {
		double frequency = this.samplingFrequency;
		double offsetDimension = this.offsetDimension;

		Collection<NewStagerHelperTagSample> result = new LinkedList<NewStagerHelperTagSample>();

		for (NewStagerAdaptedAtom atom : atoms) {
			int length = (int) Math.round(this.widthCoeff * atom.scale * frequency);
			int structOffset = (int)(offset * offsetDimension);
			double halfLength = ((double) length) / 2;
			double pos = atom.position * frequency;


			if (pos >= halfLength) {
				structOffset += pos - Math.round(halfLength);
			}

			if (pos + halfLength > offsetDimension) {
				length = (int)(offsetDimension - pos + Math.round(halfLength));
			}

			result.add(new NewStagerHelperTagSample((int) pos, structOffset, length));
		}

		return result;
	}

}
