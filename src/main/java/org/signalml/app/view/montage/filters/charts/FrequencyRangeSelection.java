/* FrequencyRangeSelection.java created 2011-02-13
 *
 */

package org.signalml.app.view.montage.filters.charts;

/**
 *
 * @author Piotr Szachewicz
 */
public class FrequencyRangeSelection {

	private double lowerFrequency;
	private double higherFrequency;

	public FrequencyRangeSelection(double frequency1, double frequency2) {
		if (frequency1 <= frequency2) {
			this.lowerFrequency = frequency1;
			this.higherFrequency = frequency2;
		}
		else {
			this.higherFrequency = frequency1;
			this.lowerFrequency = frequency2;
		}

	}

	public double getLowerFrequency() {
		return lowerFrequency;
	}

	public double getHigherFrequency() {
		return higherFrequency;
	}
}
