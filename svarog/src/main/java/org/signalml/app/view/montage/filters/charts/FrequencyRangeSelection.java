/* FrequencyRangeSelection.java created 2011-02-13
 *
 */

package org.signalml.app.view.montage.filters.charts;

/**
 * This class represents a frequency range.
 *
 * @author Piotr Szachewicz
 */
public class FrequencyRangeSelection {

	/**
	 * The lower frequency which limits the frequency range.
	 */
	private double lowerFrequency;

	/**
	 * The higher frequency which limits the frequency range.
	 */
	private double higherFrequency;

	/**
	 * Constructor. The ordering of the frequency doesn't matter.
	 * @param frequency1 frequency limiting the frequency range
	 * @param frequency2 frequency limiting the frequency range
	 */
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

	/**
	 * Returns the lower frequency which limits the frequency range.
	 * @return the lower frequency which limits the frequency range
	 */
	public double getLowerFrequency() {
		return lowerFrequency;
	}

	/**
	 * Returns the higher frequency which limits the frequency range.
	 * @return the higher frequency which limits the frequency range
	 */
	public double getHigherFrequency() {
		return higherFrequency;
	}

}
