/* TransferFunction.java created 2010-11-30
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import flanagan.complex.Complex;
import flanagan.complex.ComplexPoly;

/**
 * This class represents a complex frequency response of a filter. Contains two
 * arrays - one of them holds the frequencies at which the frequency response
 * was computed (from 0 to PI), the other one holds the frequency response for
 * those frequencies (each number is a complex number).
 * @author Piotr Szachewicz
 */
public class TransferFunction {

	/**
	 * The coefficients of the filter for which this transfer function
	 * is calculated.
	 */
	protected FilterCoefficients filterCoefficients;

	/**
	 * an array of frequencies at which the frequency response was computed
	 */
	protected double[] frequencies;

	/**
	 * an array holding the complex frequency response
	 */
	protected Complex[] gain;

	/**
	 * Constructor. Creates an empty {@link TransferFunction} which can
	 * contain specified number of points.
	 *
	 * @param numberOfPoints number of frequencies at which the frequency response
	 * will be computed
	 */
	TransferFunction(int numberOfPoints, FilterCoefficients filterCoefficients) {
		frequencies = new double[numberOfPoints];
		gain = new Complex[numberOfPoints];
		this.filterCoefficients = filterCoefficients;
		calculateTransferFunction();
	}

	/**
	 * Calculates the {@link TransferFunction complex frequency response}
	 * for the parameters given to  to the constructor.
	 */
	protected void calculateTransferFunction() {

		ComplexPoly numerator = new ComplexPoly(filterCoefficients.getBCoefficients());
		ComplexPoly denominator = new ComplexPoly(filterCoefficients.getACoefficients());

		double frequency;
		Complex exponent = new Complex();
		for (int i = 0; i < frequencies.length; i++) {
			frequency = i * Math.PI / frequencies.length;
			exponent = Complex.exp(new Complex(0, -frequency));
			setValue(i, frequency, Complex.over(numerator.evaluate(exponent), denominator.evaluate(exponent)));
		}

	}

	/**
	 * Sets the value for the specified elements in the frequency and
	 * gain arrays.
	 *
	 * @param i the index of the element to change
	 * @param frequency the new value of frequency to be put in the array
	 * @param value the new value of gain to be put in the array
	 */
	protected void setValue(int i, double frequency, Complex value) {
		frequencies[i] = frequency;
		gain[i] = value;
	}

	/**
	 * Returns how much points this frequency response holds
	 *
	 * @return the number of points this frequency response holds.
	 */
	public int getSize() {
		if (frequencies != null)
			return frequencies.length;
		return 0;
	}

	/**
	 * Returns an array containing the frequencies at which this
	 * frequency response was calculated.
	 *
	 * @return an array of frequencies
	 */
	public double[] getFrequencies() {
		return frequencies;
	}

	/**
	 * Returns the specified element of the frequency array.
	 *
	 * @param number the index of the element to be returned
	 * @return the specified element of the frequency array
	 */
	public double getFrequency(int number) {
		return frequencies[number];
	}

	/**
	 * Returns an array containing the frequency response.
	 *
	 * @return the frequency response
	 */
	public Complex[] getGain() {
		return gain;
	}

	/**
	 * Returns the specified element of the gain array.
	 *
	 * @param number the index of the element to be returned
	 * @return the specified element of the gain array
	 */
	public Complex getGain(int number) {
		return gain[number];
	}

}