/* FilterFrequencyResponse.java created 2010-09-27
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

/**
 * This class holds a representation of a filter frequency response. It contains
 * two arrays - one holding frequencies at which the response was calculated,
 * the other holds the frequency response.
 *
 * @author Piotr Szachewicz
 */
public class FilterFrequencyResponse {

	/**
	 * an array of frequencies at which the frequency response was calculated
	 */
	protected double[] frequencies;

	/**
	 * an array containing the calculated frequency response
	 */
	protected double[] gain;

	/**
	 * Constructor. Creates an empty {@link FilterFrequencyResponse} which can
	 * contain specified number of points.
	 *
	 * @param numberOfPoints number of frequencies at which the frequency response
	 * will be computed
	 */
	FilterFrequencyResponse(int numberOfPoints) {
		frequencies = new double[numberOfPoints];
		gain = new double[numberOfPoints];
	}

	/**
	 * Sets the value of the specified elements in the frequency and
	 * gain arrays.
	 *
	 * @param i the index of the element to change
	 * @param frequency the new value of frequency to be put in the array
	 * @param value the new value of gain to be put in the array
	 */
	public void setValue(int i, double frequency, double value) {
		frequencies[i] = frequency;
		gain[i] = value;
	}

	/**
	 * Sets the value of the specified element in the frequency array.
	 *
	 * @param i the index of the element to change
	 * @param frequency the new value of frequency
	 */
	public void setFrequency(int i, double frequency) {
		frequencies[i] = frequency;
	}

	/**
	 * Sets the value of the specified element in the gain arrray.
	 *
	 * @param i the index of the element to change
	 * @param newGain the new value of gain
	 */
	public void setGain(int i, double newGain) {
		gain[i] = newGain;
	}

	/**
	 * Returns how much points this frequency response can hold
	 *
	 * @return the number of points this frequency response can hold.
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
	 * Returns an array containing the frequency response.
	 *
	 * @return the frequency response
	 */
	public double[] getGain() {
		return gain;
	}

}
