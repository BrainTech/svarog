package org.signalml.domain.signal.filter.iir.helper;

import org.signalml.domain.signal.filter.SamplesWithOffset;
import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.domain.signal.samplesource.SampleSourceEngine;
import org.signalml.math.ArrayOperations;
import org.signalml.math.iirdesigner.FilterCoefficients;

/**
 * Adds additional samples to a sample source so that filtering it
 * with an IIR filter will be more stable.
 *
 * @author Piotr Szachewicz
 */
public class GrowSignalSampleSource extends SampleSourceEngine {

	protected double[] bCoefficients;
	protected double[] aCoefficients;

	/**
	 * The samples added to the left of the original sample source.
	 */
	protected double[] leftExtension;
	/**
	 * The samples added to the right of the original sample source.
	 */
	protected double[] rightExtension;

	/**
	 * Constructor.
	 * @param source the sample source which should be enriched by the additional samples.
	 * @param filterCoefficients the coefficients of the filter for which the stability
	 * should be optimized after growing the sample source.
	 */
	public GrowSignalSampleSource(SampleSource source, FilterCoefficients filterCoefficients) {
		super(source);

		this.bCoefficients = filterCoefficients.getBCoefficients();
		this.aCoefficients = filterCoefficients.getACoefficients();

		int ntaps = Math.max(aCoefficients.length, bCoefficients.length);
		int edge = ntaps * 3;
		calculateEdges(edge);
	}

	/**
	 * Calculates the samples that should be added to the left
	 * and right of the original sample source.
	 * @param edgeSize number of samples that should be added to each side.
	 */
	protected void calculateEdges(int edgeSize) {

		double[] leftEnd = new double[1];
		leftExtension = new double[edgeSize];
		source.getSamples(leftEnd, 0, 1, 0);
		source.getSamples(leftExtension, 1, edgeSize, 0);
		leftExtension = ArrayOperations.reverse(leftExtension);

		double[] rightEnd = new double[1];
		rightExtension = new double[edgeSize];
		source.getSamples(rightEnd, source.getSampleCount()-1, 1, 0);
		source.getSamples(rightExtension, source.getSampleCount()-edgeSize-1, edgeSize, 0);
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

	/**
	 * Returns the samples from the {@link GrowSignalSampleSource#leftExtension}
	 * that intersect with the given signalOffset and count.
	 * @param signalOffset the first sample which is requested in the invocation
	 * of the {@link GrowSignalSampleSource#getSamples(double[], int, int, int)}
	 * method.
	 *
	 * @param count the number of samples requested in the
	 * {@link GrowSignalSampleSource#getSamples(double[], int, int, int)}
	 * @return the samples from the left extension.
	 */
	private double[] getLeftPart(int signalOffset, int count) {

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

	/**
	 * Returns the samples from the original sample source
	 * that intersect with the given signalOffset and count.
	 * @param signalOffset the first sample which is requested in the invocation
	 * of the {@link GrowSignalSampleSource#getSamples(double[], int, int, int)}
	 * method.
	 *
	 * @param count the number of samples requested in the
	 * {@link GrowSignalSampleSource#getSamples(double[], int, int, int)}
	 * @return the samples from the left extension.
	 */
	private SamplesWithOffset getCenterPart(int signalOffset, int count) {
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
		return new SamplesWithOffset(center, source.getSamples(center, centerBegin, centerSize, 0));
	}

	/**
	 * Returns the samples from the {@link GrowSignalSampleSource#rightExtension}
	 * that intersect with the given signalOffset and count.
	 * @param signalOffset the first sample which is requested in the invocation
	 * of the {@link GrowSignalSampleSource#getSamples(double[], int, int, int)}
	 * method.
	 *
	 * @param count the number of samples requested in the
	 * {@link GrowSignalSampleSource#getSamples(double[], int, int, int)}
	 * @return the samples from the left extension.
	 */
	private double[] getRightPart(int signalOffset, int count) {
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
	public long getSamples(double[] target, int signalOffset, int count, int arrayOffset) {

		double[] left = getLeftPart(signalOffset, count);
		SamplesWithOffset centerPart = getCenterPart(signalOffset, count);
		double[] center = centerPart.samples;
		double[] right = getRightPart(signalOffset, count);

		System.arraycopy(left, 0, target, 0, left.length);
		System.arraycopy(center, 0, target, left.length, center.length);
		System.arraycopy(right, 0, target, left.length+center.length, right.length);

		return centerPart.offset;
	}
}
