package org.signalml.domain.signal.filter.iir.helper;

import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.domain.signal.samplesource.SampleSourceEngine;
import org.signalml.math.ArrayOperations;
import org.signalml.math.iirdesigner.FilterCoefficients;

public class GrowSignalSampleSource extends SampleSourceEngine {

	protected double[] bCoefficients;
	protected double[] aCoefficients;

	protected double[] leftExtension;
	protected double[] rightExtension;

	public GrowSignalSampleSource(SampleSource source, FilterCoefficients filterCoefficients) {
		super(source);

		this.bCoefficients = filterCoefficients.getBCoefficients();
		this.aCoefficients = filterCoefficients.getACoefficients();

		int ntaps = Math.max(aCoefficients.length, bCoefficients.length);
		int edge = ntaps * 3;
		calculateEdges(edge);
	}

	protected void calculateEdges(int edge) {

		double[] leftEnd = new double[1];
		leftExtension = new double[edge];
		source.getSamples(leftEnd, 0, 1, 0);
		source.getSamples(leftExtension, 1, edge, 0);
		leftExtension = ArrayOperations.reverse(leftExtension);

		double[] rightEnd = new double[1];
		rightExtension = new double[edge];
		source.getSamples(rightEnd, source.getSampleCount()-1, 1, 0);
		source.getSamples(rightExtension, source.getSampleCount()-edge-1, edge, 0);
		rightExtension = ArrayOperations.reverse(rightExtension);

		for (int i = 0; i < leftExtension.length; i++)
			leftExtension[i] = 2 * leftEnd[0] - leftExtension[i];

		for (int i = 0; i < rightExtension.length; i++)
			rightExtension[i] = 2 * rightEnd[0] - rightExtension[i];

	}

	@Override
	public int getSampleCount() {
		return super.getSampleCount() + leftExtension.length + rightExtension.length;
	}

	protected double[] getLeftPart(int signalOffset, int count) {

		int leftBegin = signalOffset;
		if (leftBegin > leftExtension.length)
			leftBegin = leftExtension.length;

		int leftEnd = leftBegin + count;
		if (leftEnd > leftExtension.length)
			leftEnd = leftExtension.length;

		int size = leftEnd - leftBegin;
		double[] left = new double[size];
		System.arraycopy(leftExtension, leftBegin, left, 0, size);
		return left;
	}

	protected double[] getCenterPart(int signalOffset, int count) {
		int centerBegin = signalOffset - leftExtension.length;
		if (centerBegin < 0)
			centerBegin = 0;
		else if (centerBegin >= source.getSampleCount())
			centerBegin = source.getSampleCount();

		int centerEnd = signalOffset + count - leftExtension.length;
		if (centerEnd < 0)
			centerEnd = 0;
		else if (centerEnd >= source.getSampleCount())
			centerEnd = source.getSampleCount();

		int centerSize = centerEnd - centerBegin;
		double[] center = new double[centerSize];
		source.getSamples(center, centerBegin, centerSize, 0);

		return center;
	}

	protected double[] getRightPart(int signalOffset, int count) {
		int rightBegin = signalOffset - leftExtension.length - source.getSampleCount();
		if (rightBegin < 0)
			rightBegin = 0;

		int rightEnd = signalOffset - leftExtension.length - source.getSampleCount() + count;
		if (rightEnd < 0)
			rightEnd = 0;

		int size = rightEnd - rightBegin;
		double[] right = new double[size];
		System.arraycopy(rightExtension, rightBegin, right, 0, size);

		return right;
	}

	@Override
	public void getSamples(double[] target, int signalOffset, int count, int arrayOffset) {

		double[] left = getLeftPart(signalOffset, count);
		double[] center = getCenterPart(signalOffset, count);
		double[] right = getRightPart(signalOffset, count);

		System.arraycopy(left, 0, target, 0, left.length);
		System.arraycopy(center, 0, target, left.length, center.length);
		System.arraycopy(right, 0, target, left.length+center.length, right.length);
	}

}
