/* FilterFrequencyResponse.java created 2010-09-27
 *
 */

package org.signalml.math.iirdesigner;

/**
 * This class can hold a representation of various filter frequency responses
 * (like frequency response, phase shift, group delay). It contains
 * two arrays - one holding frequencies at which the response was calculated,
 * the other holds the response (gain or phase response).
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
	protected double[] values;

	/**
	 * Constructor. Creates an empty {@link FilterFrequencyResponse} which can
	 * contain specified number of points.
	 *
	 * @param numberOfPoints number of frequencies at which the frequency response
	 * will be computed
	 */
	FilterFrequencyResponse(int numberOfPoints) {
		frequencies = new double[numberOfPoints];
		values = new double[numberOfPoints];
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
	 * Sets the value of the specified element in the frequency array.
	 *
	 * @param i the index of the element to change
	 * @param frequency the new value of frequency
	 */
	public void setFrequency(int i, double frequency) {
		frequencies[i] = frequency;
	}

	/**
	 * Sets the frequencies for this frequency response. The new frequency
	 * array size must be equal to the number of points given to the
	 * constructor.
	 * @param frequencies the new frequencies values for this frequency
	 * response
	 */
	public void setFrequencies(double[] frequencies) {
		/*the number of points was set in the constructor
		 * should not change.
		 */
		assert(frequencies.length == this.frequencies.length);

		this.frequencies = frequencies;
	}

	/**
	 * Returns the value of the specified element in the frequency response
	 * (values).
	 * @param i the index of the element to be returned
	 * @return the value in the frequency response having the given index
	 */
	public double getValue(int i) {
		return values[i];
	}

	/**
	 * Returns an array containing the frequency response.
	 *
	 * @return the frequency response
	 */
	public double[] getValues() {
		return values;
	}

	/**
	* Sets the value of the specified element in the gain arrray.
	*
	* @param i the index of the element to change
	* @param newValue the new value of gain
	*/
	public void setValue(int i, double newValue) {
		values[i] = newValue;
	}

	/**
	 * Sets the values of gain.
	 * @param values the values to which the gain should be set (new values
	 * array size must be equal to the number of points given to the
	 * constructor)
	 */
	public void setValues(double[] values) {
		assert(frequencies.length == values.length);

		this.values = values;
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
		values[i] = value;
	}
}
