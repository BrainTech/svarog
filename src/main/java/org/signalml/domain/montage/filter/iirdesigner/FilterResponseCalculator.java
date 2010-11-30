/* FilterResponseCalculator.java created 2010-11-30
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

/**
 *
 * @author Piotr Szachewicz
 */
public class FilterResponseCalculator {

	private int numberOfPoints;
	private double samplingFrequency;

	private TransferFunction transferFunction;

	public FilterResponseCalculator(int numberOfPoints, double samplingFrequency, FilterCoefficients filterCoefficients) {
		this.numberOfPoints = numberOfPoints;
		this.samplingFrequency = samplingFrequency;

		transferFunction = new TransferFunction(numberOfPoints, filterCoefficients);
	}

	/**
	 *  Returns the frequency response of the filter represented by these coefficients.
	 *
	 * @param numberOfPoints number of frequencies at which the frequency response will be calculated
	 * @param samplingFrequency the sampling frequency for which to calculate the frequency response
	 *
	 * @return the {@link FilterFrequencyResponse frequency response} of the filter
	 */
	public FilterFrequencyResponse getFrequencyResponse() {

		FilterFrequencyResponse frequencyResponse = new FilterFrequencyResponse(numberOfPoints);

		for (int i = 0; i < transferFunction.getSize(); i++) {
			frequencyResponse.setFrequency(i, samplingFrequency / (2 * Math.PI) * transferFunction.getFrequency(i));
			frequencyResponse.setValues(i, 20 * Math.log10(transferFunction.getGain(i).abs()));
		}

		return frequencyResponse;

	}

	public FilterFrequencyResponse getPhaseResponse() {

		FilterFrequencyResponse frequencyResponse = new FilterFrequencyResponse(numberOfPoints);

		double phaseDelay;

		for (int i = 0; i < transferFunction.getSize(); i++) {
			frequencyResponse.setFrequency(i, samplingFrequency / (2 * Math.PI) * transferFunction.getFrequency(i));

			phaseDelay = Math.atan(transferFunction.getGain(i).getImag() /
				transferFunction.getGain(i).getReal());
			phaseDelay = Math.toDegrees(phaseDelay);
			frequencyResponse.setValues(i,phaseDelay);
		}

		return frequencyResponse;

	}




}
