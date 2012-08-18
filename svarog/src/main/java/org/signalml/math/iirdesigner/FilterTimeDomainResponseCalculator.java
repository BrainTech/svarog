/* FilterTimeDomainResponseCalculator.java created 2011-02-05
 *
 */

package org.signalml.math.iirdesigner;

import org.signalml.domain.signal.filter.iir.AbstractIIRSinglechannelSampleFilter;

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
	 * Calculates the given number of values of the step function
	 * (impulse).
	 * @param excitationLength the length of the excitation
	 * @return the step excitation of a given length
	 */
	protected double[] generateStepExcitation(int excitationLength) {

		double[] excitation = new double[excitationLength];

		for (int i = 0; i < excitation.length; i++)
			excitation[i] = 1.0;

		return excitation;

	}

	/**
	 * Returns the step response of the filter.
	 * @param numberOfPoints the length of the filter time domain response
	 * @return the step response of the filter
	 */
	public FilterTimeDomainResponse getStepResponse(int numberOfPoints) {

		double[] excitation = generateStepExcitation(numberOfPoints);
		double[] stepResponseAmplitudes = AbstractIIRSinglechannelSampleFilter.filter(filterCoefficients.getBCoefficients(), filterCoefficients.getACoefficients(), excitation);

		return new FilterTimeDomainResponse(stepResponseAmplitudes, samplingFrequency);

	}

	/**
	 * Calculates the given number of values of the Dirac delta function
	 * (impulse).
	 * @param excitationLength the length of the excitation
	 * @return the impulse excitation of a given length
	 */
	protected double[] generateImpulseExcitation(int excitationLength) {

		double[] excitation = new double[excitationLength];

		excitation[0] = 1.0;
		for (int i = 1; i < excitation.length; i++)
			excitation[i] = 0.0;

		return excitation;

	}

	/**
	 * Returns the impulse response of the filter.
	 * @param numberOfPoints the length of the filter time domain response
	 * @return the impulse response of the filter
	 */
	public FilterTimeDomainResponse getImpulseResponse(int numberOfPoints) {

		double[] excitation = generateImpulseExcitation(numberOfPoints);
		double[] impulseResponseAmplitudes = AbstractIIRSinglechannelSampleFilter.filter(filterCoefficients.getBCoefficients(), filterCoefficients.getACoefficients(), excitation);

		FilterTimeDomainResponse stepResponse = new FilterTimeDomainResponse(impulseResponseAmplitudes, samplingFrequency);
		return stepResponse;

	}

}
