/* FilterTimeDomainResponseCalculator.java created 2011-02-05
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.signalml.domain.signal.TimeDomainSampleFilterEngine;

/**
 * This class represents a calculator capable of calculating time domain responses
 * of a given filter (step response or impulse response).
 *
 * @author Piotr Szachewicz
 */
public class FilterTimeDomainResponseCalculator extends FilterResponseCalculator {

	/**
	 * Constructor. Creates a new calculator for a given filter.
	 * @param samplingFrequency sampling frequency of the signal
	 * @param filterCoefficients the coefficients of the filter for which
	 * the filter responses will be calculated
	 */
	public FilterTimeDomainResponseCalculator(double samplingFrequency, FilterCoefficients filterCoefficients) {
		super(samplingFrequency, filterCoefficients);
	}

	/**
	 * Returns the step response of the filter.
	 * @param numberOfPoints the length of the filter time domain response
	 * @return the step response of the filter
	 */
	public FilterTimeDomainResponse getStepResponse(int numberOfPoints) {

		double[] input = new double[numberOfPoints];

		for(int i = 0; i < input.length; i++)
			input[i] = 1.0;

		double[] stepResponseAmplitudes = TimeDomainSampleFilterEngine.filter(filterCoefficients.getBCoefficients(), filterCoefficients.getACoefficients(), input);

		FilterTimeDomainResponse stepResponse = new FilterTimeDomainResponse(stepResponseAmplitudes, samplingFrequency);
		return stepResponse;

	}

	/**
	 * Returns the impulse response of the filter.
	 * @param numberOfPoints the length of the filter time domain response
	 * @return the impulse response of the filter
	 */
	public FilterTimeDomainResponse getImpulseResponse(int numberOfPoints) {

		double[] input = new double[numberOfPoints];

		input[0] = 1.0;
		for(int i = 1; i < input.length; i++)
			input[i] = 0.0;

		double[] impulseResponseAmplitudes = TimeDomainSampleFilterEngine.filter(filterCoefficients.getBCoefficients(), filterCoefficients.getACoefficients(), input);

		FilterTimeDomainResponse stepResponse = new FilterTimeDomainResponse(impulseResponseAmplitudes, samplingFrequency);
		return stepResponse;

	}

}
