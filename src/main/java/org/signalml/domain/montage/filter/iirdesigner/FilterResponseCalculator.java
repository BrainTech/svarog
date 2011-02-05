/* FilterResponseCalculator.java created 2011-02-05
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

/**
 * An abstract class for creating concrete calculators: {@link FilterFrequencyResponseCalculator}
 * or {@link FilterTimeDomainResponseCalculator}.
 *
 * @author Piotr Szachewicz
 */
public abstract class FilterResponseCalculator {

	/**
	 * The sampling frequency for which the filter responses will be calculated.
	 */
	protected double samplingFrequency;

	/**
	 * The coefficients of the filter for which the filter responses
	 * are calculated.
	 */
	protected FilterCoefficients filterCoefficients;

	/**
	 * Constructor. Creates a new FilterResponseCalculator.
	 * @param samplingFrequency sampling frequency for which the responses
	 * will be calculated.
	 * @param filterCoefficients coefficients of the filter for which the
	 * responses will be calculated.
	 */
	public FilterResponseCalculator(double samplingFrequency, FilterCoefficients filterCoefficients) {
		this.samplingFrequency = samplingFrequency;
		this.filterCoefficients = filterCoefficients;
	}

}
