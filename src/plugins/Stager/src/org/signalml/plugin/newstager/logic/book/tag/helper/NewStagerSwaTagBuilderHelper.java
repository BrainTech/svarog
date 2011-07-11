package org.signalml.plugin.newstager.logic.book.tag.helper;

import java.util.BitSet;

import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagHelperData;
import org.signalml.plugin.newstager.data.tag.NewStagerHelperTagSample;

public class NewStagerSwaTagBuilderHelper extends
	NewStagerBookAtomAbstractTagHelper implements
	INewStagerBookAtomCoeffHelper {

	private final INewStagerBookAtomPrimaryTagHelper helper;
	private final int signalOffset;

	public NewStagerSwaTagBuilderHelper(NewStagerBookAtomTagHelperData data,
					    INewStagerBookAtomPrimaryTagHelper helper, int signalOffset) {
		super(data);
		this.helper = helper;
		this.signalOffset = signalOffset;
	}

	private NewStagerBookSample sample;
	private double result;

	@Override
	public double convertToValue(NewStagerBookSample sample) {
		if (this.sample != sample) {
			this.sample = sample;
			this.compute(sample);
		}
		return this.result;
	}

	private void compute(NewStagerBookSample sample) {
		int offsetDimension = (int) this.data.bookInfo.offsetDimension;
		BitSet bits = new BitSet(offsetDimension);

		for (NewStagerHelperTagSample tagSample : this.helper
				.convertToTagSamples(sample)) { // TODO change swa_width
			int offset = tagSample.offset;
			int length = tagSample.length;
			int end = (tagSample.position + ((double) length) / 2) > offsetDimension ? ((sample.offset + 1)
					* offsetDimension - 1)
				  : (offset + length);

			offset -= (sample.offset + signalOffset) * offsetDimension;
			end -= (sample.offset + signalOffset) * offsetDimension;
			if (offset <= offsetDimension && end >= 0) {
				bits.set(Math.max(0, offset),
					 Math.min(offsetDimension - 1, end) + 1);
			}
		}

		this.result = ((double) bits.cardinality()) / offsetDimension;
	}
}
