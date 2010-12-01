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

	public FilterFrequencyResponse getPhaseShiftInDegrees() {

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

	public FilterFrequencyResponse getPhaseShiftInMilliseconds() {

		FilterFrequencyResponse phaseShift = this.getPhaseShiftInDegrees();
		double[] values = phaseShift.getValues();
		double period;

		for (int i = 0; i < values.length; i++) {
			period = 1 / frequencies[i];
			values[i] = period * values[i] / 360.0;
			values[i] = 1000 * values[i]; //convert from seconds to milliseconds
		}

		phaseShift.setValues(values);
		return phaseShift;

	}

	public FilterFrequencyResponse getGroupDelayResponse() {

		FilterFrequencyResponse groupDelay = getPhaseShiftInMilliseconds();
		return groupDelay;

		//groupDelay

		/*FilterFrequencyResponse groupDelay = getPhaseShiftInDegrees();

		double[] values = groupDelay.getValues();
		int n = values.length;

		values[0] = -1 * ((values[1] - values[0])
			/ (frequencies[1] - frequencies[0])) / 360;

		for (int i = 1; i < n - 1; i++) {
			values[i] = -1 * (((values[i] - values[i - 1]) / (frequencies[i] - frequencies[i - 1]))
				+ ((values[i + 1] - values[i]) / (frequencies[i + 1] - frequencies[i]))) / (2 * 360);
		}

		values[n - 1] = -1 * ((values[n - 1] - values[n - 2])
			/ (frequencies[n - 1] - frequencies[n - 2])) / 360;

		groupDelay.setValues(values);
		return groupDelay;*/

	}
}
