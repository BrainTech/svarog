/* FilterTimeResponse.java created 2011-02-05
 *
 */

package org.signalml.math.iirdesigner;

/**
 * This class can hold a representation of various filter time domain
 * responses like step response or impulse response.
 * It contains two arrays - one holding the time values for which the response
 * was calculated, the other holds the signal values of the the response.
 *
 * @author Piotr Szachewicz
 */
public class FilterTimeDomainResponse {

	/**
	 * If a filter time response has values above this threshold, the filter
	 * is considered to be instable.
	 */
	public static double INSTABILITY_THRESHOLD = 10e10;

	/**
	 * An array containing the time values for which the time domain response
	 * was calculated.
	 */
	protected double[] time;

	/**
	 * An array containing the signal values of the time domain filter response.
	 */
	protected double[] values;

	/**
	 * Creates a new time domain response.
	 * @param numberOfPoints the number of points for which the time domain
	 * response was calculated
	 * @param samplingFrequency the sampling frequency of the signal
	 */
	public FilterTimeDomainResponse(int numberOfPoints, double samplingFrequency) {
		time = new double[numberOfPoints];
		values = new double[numberOfPoints];

		calculateTime(samplingFrequency);
	}

	/**
	 * Creates a new time domain response.
	 * @param values the values of the time domain response
	 * @param samplingFrequency the sampling frequency of the signal
	 */
	public FilterTimeDomainResponse(double[] values, double samplingFrequency) {
		this(values.length, samplingFrequency);

		this.values = values;
	}

	/**
	 * Calculates the time values for this response.
	 * @param samplingFrequency the sampling frequency of the signal
	 */
	protected void calculateTime(double samplingFrequency) {
		for (int i = 0; i < time.length; i++) {
			time[i] = (i) / samplingFrequency;
		}
	}

	/**
	 * Returns the time values for the signal (in seconds).
	 * @return an array containing time value of each sample
	 */
	public double[] getTime() {
		return time;
	}

	/**
	 * Returns the time domain response.
	 * @return an array containing the time domain response
	 */
	public double[] getValues() {
		return values;
	}

	/**
	 * Returns whether this filter is stable or instable.
	 * @return true if the filter is stable.
	 */
	public boolean isStable() {
		for (int i = 0; i < values.length; i++)
			if (Math.abs(values[i]) > INSTABILITY_THRESHOLD)
				return false;
		return true;
	}

	public int getIndexOfFirstSampleAboveInstabilityThreshold() {
		for (int i = 0; i < values.length; i++)
			if (Math.abs(values[i]) > INSTABILITY_THRESHOLD)
				return i;
		return values.length;
	}

}
