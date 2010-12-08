/* FilterResponseCalculator.java created 2010-11-30
 *
 */
package org.signalml.domain.montage.filter.iirdesigner;

import flanagan.complex.Complex;
import flanagan.math.FourierTransform;

/**
 * This class represents a calculator capable of computing various filter
 * frequency response for filter coefficients given in the constructor.
 * The available filter responses include: magnitude frequency response,
 * phase shift frequency response and group delay.
 *
 * @author Piotr Szachewicz
 */
public class FilterResponseCalculator {

	/**
	 * The number of points the filter responses calculated using
	 * this FilterResponseCalculator would have.
	 */
	private int numberOfPoints;

	/**
	 * The sampling frequency for which the filter responses will be calculated.
	 */
	private double samplingFrequency;

	/**
	 * The coefficients of the filter for which the filter responses
	 * are calculated.
	 */
	private FilterCoefficients filterCoefficients;

	/**
	 * The transfer function of this filter.
	 */
	private TransferFunction transferFunction;

	/**
	 * The frequencies vector for frequency responses calculated using
	 * this FilterResponseCalculator.
	 */
	private double[] frequencies;

	/**
	 * Constructor.
	 * @param numberOfPoints the number of values for which the filter
	 * responses will be calculated (equal to the size of the arrays
	 * containing the responses)
	 * @param samplingFrequency the sampling frequency of the signal for
	 * which the filter responses will be calculated
	 * @param filterCoefficients the coefficients of the filter for which
	 * the filter responsess will be calculated
	 */
	public FilterResponseCalculator(int numberOfPoints, double samplingFrequency, FilterCoefficients filterCoefficients) {
		this.numberOfPoints = numberOfPoints;
		this.samplingFrequency = samplingFrequency;

		this.filterCoefficients = filterCoefficients;
		transferFunction = new TransferFunction(numberOfPoints, filterCoefficients);
		calculateFrequencies();
	}

	/**
	 * Precalculates the values for the frequencies.
	 */
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

	/**
	 * Returns the filter phase shift in degrees.
	 * @return the phase delay
	 */
	public FilterFrequencyResponse getPhaseShiftInDegrees() {

		FilterFrequencyResponse frequencyResponse = new FilterFrequencyResponse(numberOfPoints);

		double phaseDelay;

		frequencyResponse.setFrequencies(frequencies);
		for (int i = 0; i < transferFunction.getSize(); i++) {
			phaseDelay = Math.toDegrees(transferFunction.getGain(i).arg());
			frequencyResponse.setValue(i, phaseDelay);
		}

		frequencyResponse.setValues(SpecialMath.unwrap(frequencyResponse.getValues()));

		return frequencyResponse;

	}

	/**
	 * Returns the group delay characterizing the filter given in the
	 * constructor.
	 * @return the group delay for the filter given in the constructor
	 */
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
