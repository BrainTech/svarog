package org.signalml.domain.signal.filter;

import org.apache.log4j.Logger;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.samplesource.RoundBufferSampleSource;
import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.math.ArrayOperations;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.InitalStateCalculator;

/**
 * This class represents a Time Domain (IIR or FIR) engine for filtering the samples.
 * Use this for offline signals.
 *
 * @author Piotr Szachewicz
 */
public class OfflineTimeDomainSampleFilterEngine extends AbstractTimeDomainSampleFilterEngine {

	protected static final Logger logger = Logger.getLogger(OfflineTimeDomainSampleFilterEngine.class);

	/**
	 * The signal offset of the first sample stored in the {@link AbstractTimeDomainSampleFilterEngine#filtered}
	 * sample source. Allows to control the caching of the filtered data.
	 */
	private Integer filteredSignalOffset = null;

	/**
	 * True if filtfilt filtering should be used while
	 * filtering offline data.
	 */
	protected boolean useFiltFilt = false;

	public OfflineTimeDomainSampleFilterEngine(SampleSource source, TimeDomainSampleFilter definition) {
		super(source, definition);
	}

	public OfflineTimeDomainSampleFilterEngine(SampleSource source, TimeDomainSampleFilter definition, FilterCoefficients coefficients) {
		super(source, definition, coefficients);
	}

	public OfflineTimeDomainSampleFilterEngine(SampleSource source, FilterCoefficients coefficients) {
		super(source, coefficients);
	}

	/**
	 * Returns the given number of the filtered samples starting from
	 * the given position in time. {@link OfflineTimeDomainSampleFilterEngine#updateCache(int)}
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
		if (!isCached(signalOffset, count)) {
			filterOffline(signalOffset, count);
			filteredSignalOffset = signalOffset;
			filtered.getSamples(target, 0, count, arrayOffset);
		} else {
			filtered.getSamples(target, signalOffset - filteredSignalOffset, count, arrayOffset);
		}


	}

	/**
	 * Checks whether the signal selection in question was already calculated previously
	 * and is available in the filtered samples cache.
	 * @param signalOffset the position (in time) in the signal starting
	 * from which samples will be returned
	 * @param count the number of samples to be returned
	 * @return true if the samples are available in cache, false otherwise
	 */
	protected boolean isCached(int signalOffset, int count) {
		if (filteredSignalOffset != null
				&& filteredSignalOffset <= signalOffset
				&& filteredSignalOffset + filtered.getSampleCount() >= signalOffset + count) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Filters the data from the source SampleSource. The result of filtering
	 * is put in the filtered RoundSampleSource.
	 * @param signalOffset the position (in time) in the signal starting
	 * from which samples will be returned
	 * @param count the number of filtered samples to be returned
	 */
	protected synchronized void filterOffline(int signalOffset, int count) {

		//filter signal from prefixCount samples before requested starting point (IIR filters are unstable at the beginning)
		int leftPrefixCount = 2048; //1024*2 - 2secs for 1024 samplingFreq
		if (signalOffset - leftPrefixCount < 0) //fix prefix if some starting area of the signal is requested
			leftPrefixCount = signalOffset;

		int filteredCount = leftPrefixCount + count;//samples to filter

		//get data from source and filter it
		double[] input = new double[filteredCount];
		source.getSamples(input, signalOffset - leftPrefixCount, filteredCount, 0);

		double[] newFilteredSamples = calculateInitialConditionsAndFilter(input);

		//get last filteredCount - prefixCount = count samples and return it
		filtered = new RoundBufferSampleSource(count);
		for (int i = 0; i < count; i++) {
			filtered.addSample(newFilteredSamples[i + leftPrefixCount]);
		}

	}

	/**
	 * Calculates initial conditions of the filter delays and filters the data
	 * assuming the calculated initial conditions.
	 * @param signal the signal to be filtered
	 * @return the filtered signal
	 */
	private double[] calculateInitialConditionsAndFilter(double[] signal) {
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

		if (useFiltFilt) {
			filteredSamples = ArrayOperations.reverse(filteredSamples);

			//left-wise
			double[] initialStateLeftwise = new double[initialState.length];
			for (int i = 0; i < initialStateLeftwise.length; i++) {
				initialStateLeftwise[i] = initialState[i] * filteredSamples[filteredSamples.length - 1];
			}
			filteredSamples = filter(bCoefficients, aCoefficients, filteredSamples, initialStateLeftwise);

			filteredSamples = ArrayOperations.reverse(filteredSamples);
		}

		//shorten
		int padding = (grownSignal.length - signal.length) / 2;
		double[] result = new double[signal.length];
		System.arraycopy(filteredSamples, padding, result, 0, signal.length);
		return result;
	}

	/**
	 * Sets whether this engine should use filtfilt (forward-backward) filtering
	 * algorithm.
	 * @param filtfiltEnabled true if this engine should use filtfilt.
	 */
	public void setFiltfiltEnabled(boolean filtfiltEnabled) {
		this.useFiltFilt = filtfiltEnabled;
	}

}
