/* FilterResponseCalculator.java created 2010-11-30
 *
 */
package org.signalml.domain.montage.filter.iirdesigner;

import flanagan.complex.Complex;
import flanagan.math.FourierTransform;

/**
 *
 * @author Piotr Szachewicz
 */
public class FilterResponseCalculator {

	private int numberOfPoints;
	private double samplingFrequency;
	private FilterCoefficients filterCoefficients;
	private TransferFunction transferFunction;
	private double[] frequencies;

	public FilterResponseCalculator(int numberOfPoints, double samplingFrequency, FilterCoefficients filterCoefficients) {
		this.numberOfPoints = numberOfPoints;
		this.samplingFrequency = samplingFrequency;

		this.filterCoefficients = filterCoefficients;
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

		int fft_size = numberOfPoints * 2;

		double[] freq = new double[fft_size];
		int i;

		for (i = 0; i < fft_size; i++) {
			freq[i] = samplingFrequency * i / fft_size;
		}

		int oa = filterCoefficients.getACoefficients().length - 1;
		int oc = oa + filterCoefficients.getBCoefficients().length - 1;
		double[] c = ArrayOperations.convolve(
			filterCoefficients.getBCoefficients(),
			ArrayOperations.reverse(filterCoefficients.getACoefficients()));

		double[] cr = new double[oc + 1]; //derivative
		for (i = 0; i < cr.length; i++) {
			cr[i] = c[i] * i;
		}

		cr = ArrayOperations.padWithZeros(cr, fft_size);
		FourierTransform fourierTransform = new FourierTransform(cr);
		fourierTransform.transform();
		Complex[] num = fourierTransform.getTransformedDataAsComplex();

		c = ArrayOperations.padWithZeros(c, fft_size);
		FourierTransform fourierTransform2 = new FourierTransform(c);
		fourierTransform2.transform();
		Complex[] den = fourierTransform2.getTransformedDataAsComplex();

		double minmag = SpecialMath.getMachineEpsilon() * 10;

		for (i = 0; i < den.length; i++) {
			if (den[i].abs() < minmag) {
				System.out.println("group delay singular - setting to 0");
				num[i].reset(0, 0);
				den[i].reset(1, 0);
			}
		}

		double[] groupDelay = new double[c.length];

		for (i = 0; i < groupDelay.length; i++) {
			groupDelay[i] = (num[i].over(den[i])).getReal() - oa;
		}

		groupDelay = ArrayOperations.trimArrayToSize(groupDelay, fft_size / 2);
		freq = ArrayOperations.trimArrayToSize(freq, fft_size / 2);

		FilterFrequencyResponse filterResponse = new FilterFrequencyResponse(freq.length);
		filterResponse.setFrequencies(freq);
		filterResponse.setValues(groupDelay);

		return filterResponse;

	}
}
