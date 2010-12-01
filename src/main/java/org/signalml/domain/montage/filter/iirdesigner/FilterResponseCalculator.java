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
	private double[] frequencies;

	public FilterResponseCalculator(int numberOfPoints, double samplingFrequency, FilterCoefficients filterCoefficients) {
		this.numberOfPoints = numberOfPoints;
		this.samplingFrequency = samplingFrequency;

		transferFunction = new TransferFunction(numberOfPoints, filterCoefficients);
		calculateFrequencies();
	}

	protected void calculateFrequencies() {
		frequencies = new double[transferFunction.getSize()];

		for (int i = 0; i < frequencies.length; i++) {
			frequencies[i] = samplingFrequency / (2 * Math.PI) * transferFunction.getFrequency(i);
		}
	}

	/**
	 *  Returns the magnitude of the frequency response of the filter
	 *  set in the constructor.
	 *
	 * @return the {@link FilterFrequencyResponse frequency response} of the filter
	 */
	public FilterFrequencyResponse getMagnitudeResponse() {

		FilterFrequencyResponse frequencyResponse = new FilterFrequencyResponse(numberOfPoints);

		frequencyResponse.setFrequencies(frequencies);
		for (int i = 0; i < transferFunction.getSize(); i++) {
			frequencyResponse.setValue(i, 20 * Math.log10(transferFunction.getGain(i).abs()));
		}

		return frequencyResponse;

	}

	public FilterFrequencyResponse getPhaseResponseInDegrees() {

		FilterFrequencyResponse frequencyResponse = new FilterFrequencyResponse(numberOfPoints);

		double phaseDelay;

		frequencyResponse.setFrequencies(frequencies);
		for (int i = 0; i < transferFunction.getSize(); i++) {
			phaseDelay = transferFunction.getGain(i).argDeg();
			frequencyResponse.setValue(i, phaseDelay);
		}

		frequencyResponse.setValues(SpecialMath.unwrap(frequencyResponse.getValues()));

		return frequencyResponse;

	}

	public FilterFrequencyResponse getGroupDelayResponse() {

		FilterFrequencyResponse frequencyResponse = new FilterFrequencyResponse(numberOfPoints);

		frequencyResponse.setFrequencies(frequencies);

		frequencyResponse.setValues(SpecialMath.calculateDerivative(frequencies, getPhaseResponseInDegrees().getValues()));

		for (int i = 0; i < frequencyResponse.getSize(); i++) {
			frequencyResponse.setValue(i, -1 * frequencyResponse.getValue(i));
		}

		return frequencyResponse;
	}
}
