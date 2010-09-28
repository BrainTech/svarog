/* FilterFrequencyResponse.java created 2010-09-27
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

/**
 *
 * @author Piotr Szachewicz
 */
public class FilterFrequencyResponse {

	protected double[] frequencies;
	protected double[] gain;

	FilterFrequencyResponse(int numberOfPoints) {
		frequencies = new double[numberOfPoints];
		gain = new double[numberOfPoints];
	}

	public void setValue(int i, double frequency, double value) {
		frequencies[i] = frequency;
		gain[i] = value;
	}

	public void setFrequency(int i, double frequency) {
		frequencies[i] = frequency;
	}

	public void setGain(int i, double newGain) {
		gain[i] = newGain;
	}

	public int getSize() {
		if (frequencies != null)
			return frequencies.length;
		return 0;
	}

	public double[] getFrequencies() {
		return frequencies;
	}

	public double[] getGain() {
		return gain;
	}

}
