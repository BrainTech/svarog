/* TimeDomainSampleFilterEngine.java created 2010-08-24
 *
 */

package org.signalml.domain.signal;

import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.iirdesigner.ArrayOperations;
import org.signalml.domain.montage.filter.iirdesigner.BadFilterParametersException;
import org.signalml.domain.montage.filter.iirdesigner.FilterCoefficients;
import org.signalml.domain.montage.filter.iirdesigner.IIRDesigner;
import org.signalml.domain.montage.filter.iirdesigner.InitalStateCalculator;

/**
 * This class represents a Time Domain (IIR or FIR) filter of samples.
 * Allows to return the filtered samples based on the given source.
 *
 * @author Piotr Szachewicz
 */
public class TimeDomainSampleFilterEngine extends SampleFilterEngine {

	protected static final Logger logger = Logger.getLogger(TimeDomainSampleFilterEngine.class);

	/**
	 * Round buffer used to store filtered samples of the signal.
	 */
	protected RoundBufferSampleSource filtered = null;

	/**
	 * a Coefficients of the Time Domain filter (feedback filter coefficients).
	 */
	protected double aCoefficients[];

	/**
	 * b Coefficients of the Time Domain filter (feedforward filter coefficients).
	 */
	protected double bCoefficients[];

	/**
	 * the order of the {@link TimeDomainSampleFilter filter}.
	 */
	protected int filterOrder;
	protected boolean use_cache = true;
        protected boolean useFiltFilt = true;

	/**
	 * Constructor. Creates an engine of a filter for provided
	 * {@link SampleSource source} of samples.
	 * @param source the source of samples
	 * @param definition the {@link TimeDomainSampleFilter definition} of the
	 * filter
	 */
	public TimeDomainSampleFilterEngine(SampleSource source, TimeDomainSampleFilter definition) {

		super(source);

		this.definition = definition;

		FilterCoefficients coeffs = null;
		try {
			coeffs = IIRDesigner.designDigitalFilter(definition);
			aCoefficients = coeffs.getACoefficients();
			bCoefficients = coeffs.getBCoefficients();
			filterOrder = coeffs.getFilterOrder();
			filtered = null;

		} catch (BadFilterParametersException ex) {
			java.util.logging.Logger.getLogger(TimeDomainSampleFilterEngine.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * Constructor. Creates an engine of a filter for provided
	 * {@link FilterCoefficients filter coefficients}.
	 * @param source the source of samples
	 * @param coefficients the {@link FilterCoefficients coefficients} for which
	 * the engine will operate
	 */
	public TimeDomainSampleFilterEngine(SampleSource source, FilterCoefficients coefficients) {

		super(source);
		aCoefficients = coefficients.getACoefficients();
		bCoefficients = coefficients.getBCoefficients();
		filterOrder = coefficients.getFilterOrder();

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

		if (unfilteredSamplesNeeded > source.getSampleCount())
			zeroPaddingSize = unfilteredSamplesNeeded - source.getSampleCount();
		for (int i = 0; i < zeroPaddingSize; i++)
			unfilteredSamplesCache[i] = 0.0;

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
			for (int i = 0; i < source.getSampleCount(); i++)
				filtered.addSamples(new double[] {0.0});
		}

		if (filteredCacheSize > filtered.getSampleCount())
			zeroPaddingSize = filteredCacheSize - filtered.getSampleCount();
		for (int i = 0; i < zeroPaddingSize; i++)
			filteredSamplesCache[i] = 0.0;

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
				if (i - j < bCoefficients.length)
					filteredSamplesCache[i] += unfilteredSamplesCache[j] * bCoefficients[i - j];
				if (j < i && i -j < aCoefficients.length)
					filteredSamplesCache[i] -= filteredSamplesCache[j] * aCoefficients[i - j];
			}
			filteredSamplesCache[i] /= aCoefficients[0];

		}

		double[] newFilteredSamples = new double[newSamples];
		for (int i = 0; i < newSamples; i++)
			newFilteredSamples[i] = filteredSamplesCache[filterOrder + i];
		return newFilteredSamples;
	}

        /**
	 * Filters the given data using the specified digital filter.
	 * @param bCoefficients feedforward coefficients of the filter
	 * @param aCoefficients feedback coeffcients of the filter
	 * @param input the input signal to be filtered
	 * @return the input signal after filtering
	 */
        public static double[] filter(double[] bCoefficients, double[] aCoefficients, double[] input, double[] initialState) {

		int bi, ai, zi; //arrays indexes
		double[] filtered = new double[input.length];

		for (int n = 0; n < input.length; n++) {
			bi = 0;
			ai = 0;
			zi = 0;

			if (bCoefficients.length > 1) {
				filtered[n] = initialState[zi] + bCoefficients[bi] / aCoefficients[0] * input[n];
				bi++;
				ai++;

				for (; zi < bCoefficients.length - 2; zi++) {
					initialState[zi] = initialState[zi + 1] +
						input[n] * bCoefficients[bi] / aCoefficients[0]
						- filtered[n] * aCoefficients[ai] / aCoefficients[0];

					bi++;
					ai++;
				}
				initialState[zi] = input[n] * bCoefficients[bi] / aCoefficients[0]
					- filtered[n] * aCoefficients[ai] / aCoefficients[0];
			}
			else {
				filtered[n] = input[n] * bCoefficients[bi] / aCoefficients[0];
			}
		}

		return filtered;
        }

	/**
	 * Filters the given data using the specified digital filter.
	 * @param bCoefficients feedforward coefficients of the filter
	 * @param aCoefficients feedback coeffcients of the filter
	 * @param input the input signal to be filtered
         * is assumed.
	 * @return the input signal after filtering
	 */
	public static double[] filter(double[] bCoefficients, double[] aCoefficients, double[] input) {

		int filterOrder = Math.max(aCoefficients.length - 1, bCoefficients.length - 1);
		int cacheSize = filterOrder + input.length;
		double[] unfilteredSamplesCache = new double[cacheSize];
		double[] filteredSamplesCache = new double[cacheSize];

		for (int i = 0; i < filterOrder; i++) {
			unfilteredSamplesCache[i] = 0.0;
                }
		for (int i = filterOrder; i < unfilteredSamplesCache.length; i++)
			unfilteredSamplesCache[i] = input[i - filterOrder];

		double[] filteredSamples = calculateNewFilteredSamples(bCoefficients, aCoefficients, filterOrder, unfilteredSamplesCache, filteredSamplesCache, input.length);

		return filteredSamples;

	}

	/**
	 * Updates the cache used to store filtered samples of the signal.
	 * @param newSamples number of samples which were added to the signal since the last call
	 * of this method
	 */
	public synchronized void updateCache(int newSamples) {
		if (newSamples == 0)
			use_cache = false;
		else {
			use_cache = true;
			double[] unfilteredSamplesCache = getUnfilteredSamplesCache(newSamples);
			double[] filteredSamplesCache = getFilteredSamplesCache(newSamples);
			double[] newFilteredSamples = calculateNewFilteredSamples(bCoefficients, aCoefficients, filterOrder, unfilteredSamplesCache, filteredSamplesCache, newSamples);
			
			filtered.addSamples(newFilteredSamples);
		}

	}

	/**
	 * Return the {@link TimeDomainSampleFilter definition} of the filter.
	 * @return the {@link TimeDomainSampleFilter definition} of the filter
	 */
	@Override
	public TimeDomainSampleFilter getFilterDefinition() {
		return (TimeDomainSampleFilter)definition.duplicate();
	}

	/**
	 * Returns the given number of the filtered samples starting from
	 * the given position in time. {@link TimeDomainSampleFilterEngine#updateCache(int)}
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
		if (use_cache) { //check updateCache
			filtered.getSamples(target, signalOffset, count, arrayOffset);
		} else {
			filterOffline(target, signalOffset, count, arrayOffset);
		}
	}

        protected synchronized void filterOffline(double[] target, int signalOffset, int count, int arrayOffset) {

            //filter signal from prefixCount samples before requested starting point (IIR filters are unstable at the beginning)
                int leftPrefixCount = 10240; //1024*10 - 10secs for 1024 samplingFreq
                if (signalOffset - leftPrefixCount < 0) //fix prefix if some starting area of the signal is requested
                        leftPrefixCount = signalOffset;

                int rightPrefixCount = 0;
                if (useFiltFilt) {//for filt filt right prefix is also needed
                    rightPrefixCount = 10240;
                    if (signalOffset + count + rightPrefixCount > source.getSampleCount())
                        rightPrefixCount = source.getSampleCount() - signalOffset - count;
                }

                int filteredCount = leftPrefixCount + count + rightPrefixCount;//samples to filter

                //get data from source and filter it
                double[] input = new double[filteredCount];
                source.getSamples(input, signalOffset-leftPrefixCount, filteredCount, 0);

                double[] newFilteredSamples = null;
                if (useFiltFilt) {
                    newFilteredSamples = filterFiltFilt(input);
                }
                else {
                    newFilteredSamples = filter(bCoefficients, aCoefficients, input);
                }

                //get last filteredCount - prefixCount = count samples and return it
                filtered = new RoundBufferSampleSource(count);
                for (int i = 0; i < count; i++)
                        filtered.addSample(newFilteredSamples[i+leftPrefixCount]);

                filtered.getSamples(target, 0, count, arrayOffset);
        }

        private double[] filterFiltFilt(double[] signal) {
            InitalStateCalculator initalStateCalculator = new InitalStateCalculator(new FilterCoefficients(bCoefficients, aCoefficients));
            double[] initialState = initalStateCalculator.getInitialState();
            double[] grownSignal = initalStateCalculator.growSignal(signal);
            double[] filteredSamples;

            //right-wise
            double[] initialStateRightwise = new double[initialState.length];
            for (int i = 0; i < initialStateRightwise.length; i++) {
                initialStateRightwise[i] = initialState[i] * grownSignal[0];
	    }
            filteredSamples = filter(bCoefficients, aCoefficients, grownSignal, initialStateRightwise);

            filteredSamples = ArrayOperations.reverse(filteredSamples);

            //left-wise
            double[] initialStateLeftwise = new double[initialState.length];
            for (int i = 0; i < initialStateLeftwise.length; i++) {
                initialStateLeftwise[i] = initialState[i] * filteredSamples[filteredSamples.length - 1];
	    }
            filteredSamples = filter(bCoefficients, aCoefficients, filteredSamples, initialStateLeftwise);

            filteredSamples = ArrayOperations.reverse(filteredSamples);

            //shorten
            int padding = (grownSignal.length - signal.length) / 2;
            double[] result = new double[signal.length];
            System.arraycopy(filteredSamples, padding, result, 0, signal.length);
            return result;
        }

	public void setFiltfiltEnabled(boolean filtfiltEnabled) {
		this.useFiltFilt = filtfiltEnabled;
	}

}
