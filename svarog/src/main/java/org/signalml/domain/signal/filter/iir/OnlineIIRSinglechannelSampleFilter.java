package org.signalml.domain.signal.filter.iir;

import org.apache.log4j.Logger;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.samplesource.RoundBufferSampleSource;
import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.math.iirdesigner.FilterCoefficients;

/**
 * This class represents a Time Domain (IIR or FIR) engine for filtering the samples.
 * Use this to filter online signals.
 *
 * @author Piotr Szachewicz
 */
public class OnlineIIRSinglechannelSampleFilter extends AbstractIIRSinglechannelSampleFilter {

	protected static final Logger logger = Logger.getLogger(OnlineIIRSinglechannelSampleFilter.class);

	public OnlineIIRSinglechannelSampleFilter(SampleSource source, TimeDomainSampleFilter definition) {
		super(source, definition);
	}

	public OnlineIIRSinglechannelSampleFilter(SampleSource source, TimeDomainSampleFilter definition, FilterCoefficients coefficients) {
		super(source, definition, coefficients);
	}

	public OnlineIIRSinglechannelSampleFilter(SampleSource source, FilterCoefficients coefficients) {
		super(source, coefficients);
	}

	/**
	 * Filters the given data using the specified digital filter.
	 * This method uses previously filtered samples cache.
	 * @param bCoefficients feedforward coefficients of the filter
	 * @param aCoefficients feedback coeffcients of the filter
	 * @param input the input signal to be filtered
	 * is assumed.
	 * @return the input signal after filtering
	 */
	public static double[] filterUsingCache(double[] bCoefficients, double[] aCoefficients, double[] input) {

		int filterOrder = Math.max(aCoefficients.length - 1, bCoefficients.length - 1);
		int cacheSize = filterOrder + input.length;
		double[] unfilteredSamplesCache = new double[cacheSize];
		double[] filteredSamplesCache = new double[cacheSize];

		for (int i = 0; i < filterOrder; i++) {
			unfilteredSamplesCache[i] = 0.0;
		}
		for (int i = filterOrder; i < unfilteredSamplesCache.length; i++) {
			unfilteredSamplesCache[i] = input[i - filterOrder];
		}

		double[] filteredSamples = calculateNewFilteredSamples(bCoefficients, aCoefficients, filterOrder, unfilteredSamplesCache, filteredSamplesCache, input.length);

		return filteredSamples;

	}

	/**
	 * Returns an array containing newly added unfiltered samples which can be used to
	 * calculate a specified number of new filtered samples using the filters definition.
	 * @param newSamples number of samples which were added to the signal since
	 * the last call of this method
	 * @return an array containing enough unfiltered samples to filter newSamples of new samples
	 * (size of the array = newSamples+ order of the filter).
	 */
	protected double[] getUnfilteredSamplesCache(int newSamples) {
		int unfilteredSamplesNeeded = newSamples + filterOrder;
		int zeroPaddingSize = 0;
		double[] unfilteredSamplesCache = new double[unfilteredSamplesNeeded];

		if (unfilteredSamplesNeeded > source.getSampleCount()) {
			zeroPaddingSize = unfilteredSamplesNeeded - source.getSampleCount();
		}
		for (int i = 0; i < zeroPaddingSize; i++) {
			unfilteredSamplesCache[i] = 0.0;
		}

		source.getSamples(unfilteredSamplesCache,
						  source.getSampleCount() - unfilteredSamplesNeeded + zeroPaddingSize,
						  unfilteredSamplesNeeded - zeroPaddingSize, zeroPaddingSize);
		return unfilteredSamplesCache;

	}

	/**
	 * Returns an array with filtered samples from the filter engine's
	 * cache which can be used to calculate a specified number of new filtered
	 * samples using the filters definition.
	 *
	 * @param newSamples the number of samples which were added to the signal
	 * since the last call of this method.
	 * @return an array containing enough filtered samples to filter newSamples of new samples
	 * (size of the array = newSamples+ order of the filter. Last newSamples cells in
	 * the array is filled with zeros).
	 */
	protected double[] getFilteredSamplesCache(int newSamples) {
		int filteredCacheSize = newSamples + filterOrder;
		int zeroPaddingSize = 0;
		double[] filteredSamplesCache = new double[filteredCacheSize];

		if (filtered == null) {
			filtered = new RoundBufferSampleSource(source.getSampleCount());
			for (int i = 0; i < source.getSampleCount(); i++) {
				filtered.addSamples(new double[] {0.0});
			}
		}

		if (filteredCacheSize > filtered.getSampleCount()) {
			zeroPaddingSize = filteredCacheSize - filtered.getSampleCount();
		}
		for (int i = 0; i < zeroPaddingSize; i++) {
			filteredSamplesCache[i] = 0.0;
		}

		filtered.getSamples(filteredSamplesCache, filtered.getSampleCount() - filterOrder, filterOrder, zeroPaddingSize);
		return filteredSamplesCache;

	}

	/**
	 * Calculates newSamples of new filtered samples using a part of the unfiltered signal and
	 * part of the filtered signal stored in the cache.
	 * @param unfilteredSamplesCache an array containing unfiltered samples needed to calculate
	 * newSamples of new filtered samples.
	 * @param filteredSamplesCache an array containg filtered samples needed to calculate
	 * newSamples of new filtered samples.
	 * @param newSamples number of samples which were added to the signal since the last call
	 * of this method
	 * @return an array containing new filtered samples of the signal (size of the array = newSamples)
	 */
	protected double[] calculateNewFilteredSamples(double[] unfilteredSamplesCache,
			double[] filteredSamplesCache, int newSamples) {
		return calculateNewFilteredSamples(bCoefficients, aCoefficients, filterOrder, unfilteredSamplesCache, filteredSamplesCache, newSamples);
	}

	/**
	 * Calculates newSamples of new filtered samples using a part of the unfiltered signal and
	 * part of the filtered signal stored in the cache.
	 * @param bCoefficients b Coefficients of the filter
	 * @param aCoefficients a Coefficients of the filter
	 * @param filterOrder the order of the filter
	 * @param unfilteredSamplesCache an array containing unfiltered samples needed to calculate
	 * newSamples of new filtered samples.
	 * @param filteredSamplesCache an array containg filtered samples needed to calculate
	 * newSamples of new filtered samples.
	 * @param newSamples number of samples which were added to the signal since the last call
	 * of this method
	 * @return an array containing new filtered samples of the signal (size of the array = newSamples)
	 */
	protected static double[] calculateNewFilteredSamples(double[] bCoefficients,
			double[] aCoefficients, int filterOrder, double[] unfilteredSamplesCache,
			double[] filteredSamplesCache, int newSamples) {

		for (int i = filterOrder; i < filteredSamplesCache.length; i++) {

			for (int j = i - filterOrder; j <= i; j++) {
				if (i - j < bCoefficients.length) {
					filteredSamplesCache[i] += unfilteredSamplesCache[j] * bCoefficients[i - j];
				}
				if (j < i && i - j < aCoefficients.length) {
					filteredSamplesCache[i] -= filteredSamplesCache[j] * aCoefficients[i - j];
				}
			}
			filteredSamplesCache[i] /= aCoefficients[0];

		}

		double[] newFilteredSamples = new double[newSamples];
		for (int i = 0; i < newSamples; i++) {
			newFilteredSamples[i] = filteredSamplesCache[filterOrder + i];
		}
		return newFilteredSamples;
	}

	/**
	 * Updates the cache used to store filtered samples of the signal.
	 * @param newSamples number of samples which were added to the signal since the last call
	 * of this method
	 */
	public synchronized void updateCache(int newSamples) {
		double[] unfilteredSamplesCache = getUnfilteredSamplesCache(newSamples);
		double[] filteredSamplesCache = getFilteredSamplesCache(newSamples);
		double[] newFilteredSamples = calculateNewFilteredSamples(bCoefficients, aCoefficients, filterOrder, unfilteredSamplesCache, filteredSamplesCache, newSamples);

		filtered.addSamples(newFilteredSamples);
	}

	/**
	 * Returns the given number of the filtered samples starting from
	 * the given position in time. {@link OnlineIIRSinglechannelSampleFilter#updateCache(int)}
	 * must be run before running this method if new samples were added or
	 * cache was never updated before.
	 *
	 * @param target the array to which results will be written starting
	 * from position <code>arrayOffset</code>
	 * @param signalOffset the position (in time) in the signal starting
	 * from which samples will be returned
	 * @param count the number of samples to be returned
	 * @param arrayOffset the offset in <code>target</code> array starting
	 * from which samples will be written
	 */
	@Override
	public synchronized void getSamples(double[] target, int signalOffset, int count, int arrayOffset) {
		filtered.getSamples(target, signalOffset, count, arrayOffset);
	}

}
