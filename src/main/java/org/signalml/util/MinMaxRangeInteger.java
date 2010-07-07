/* MinMaxRangeInteger.java created 2008-02-25
 *
 */

package org.signalml.util;

import java.io.Serializable;

/** MinMaxRangeInteger
 * class implements standard mathematical interval. It can be limited from both left and right.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MinMaxRangeInteger implements Serializable {

	private int unlimitedValue;

	private static final long serialVersionUID = 1L;

	private int min;
	private int max;

	private boolean minUnlimited;
	private boolean maxUnlimited;

	/**
	 * Constructor creating range with specified value which will be the limit when eithen minimal, or maximal value is unlimited
	 * @param unlimitedValue special limit when at least one of bounds of this range is unlimited
	 */
	public MinMaxRangeInteger(int unlimitedValue) {
		this.unlimitedValue = unlimitedValue;
	}

	/**
	 * Constructor creating range with specified value which will be the limit when eithen minimal, or maximal value is unlimited,
	 * minimum and maximum of the range, boolean values telling if range is limited from left and right respectively
	 * @param unlimitedValue special limit when at least one of bounds of this range is unlimited
	 * @param min left bound of this range
	 * @param max right bound of this range
	 * @param minUnlimited boolean value which is true when range is unlimited from left, false otherwise
	 * @param maxUnlimited boolean value which is true when range is unlimited from right, false otherwise
	 */
	public MinMaxRangeInteger(int unlimitedValue, int min, int max, boolean minUnlimited, boolean maxUnlimited) {
		this.unlimitedValue = unlimitedValue;
		this.min = min;
		this.max = max;
		this.minUnlimited = minUnlimited;
		this.maxUnlimited = maxUnlimited;
	}

	/**
	 * Constructor creating range with specified value which will be the limit when eithen minimal, or maximal value is unlimited,
	 * and boolean value telling if range is limited from left and right
	 * @param unlimitedValue special limit when at least one of bounds of this range is unlimited
	 * @param unlimited boolean value which is true when range is unlimited from left and right, false otherwise
	 */
	public MinMaxRangeInteger(int unlimitedValue, boolean unlimited) {
		this.unlimitedValue = unlimitedValue;
		if (unlimited) {
			minUnlimited = true;
			maxUnlimited = true;
		}
	}

	/**
	 * Copy constructor
	 * @param template range to be copied
	 */
	public MinMaxRangeInteger(MinMaxRangeInteger template) {
		this.unlimitedValue = template.unlimitedValue;
		this.min = template.min;
		this.max = template.max;
		this.minUnlimited = template.minUnlimited;
		this.maxUnlimited = template.maxUnlimited;
	}

	/**
	 * Returns left bound of the range
	 * @return minimal value in this range
	 */
	public int getMin() {
		return min;
	}

	/**
	 * Returns left bound of the range when range is limited from left, otherwise special unlimited value
	 * @return left limit of the range
	 */
	public int getMinWithUnlimited() {
		return (minUnlimited ? unlimitedValue : min);
	}

	/**
	 * Sets left bound of the range
	 * @param min value to be set as left bound of the range
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * Set specified value as left bound of the range when it is nonnegative, otherwise range is unlimited from left
	 * @param min potential value to be set as left bound of the range
	 */
	public void setMinWithUnlimited(int min) {
		if (min < 0) {
			minUnlimited = true;
		} else {
			minUnlimited = false;
			this.min = min;
		}
	}

	/**
	 * Returns right bound of the range
	 * @return maximal value in this range
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Returns right bound of the range when range is limited from right, otherwise special unlimited value
	 * @return right limit of the range
	 */
	public int getMaxWithUnlimited() {
		return (maxUnlimited ? unlimitedValue : max);
	}

	/**
	 * Sets right bound of the range
	 * @param max value to be set as right bound of the range
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * Set specified value as right bound of the range when it is nonnegative, otherwise range is unlimited from right
	 * @param max potential value to be set as right bound of the range
	 */
	public void setMaxWithUnlimited(int max) {
		if (max < 0) {
			maxUnlimited = true;
		} else {
			maxUnlimited = false;
			this.max = max;
		}
	}

	/**
	 * Test if range is unlimited from left
	 * @return true if range is unlimited from left, otherwise false
	 */
	public boolean isMinUnlimited() {
		return minUnlimited;
	}

	/**
	 * @param minUnlimited if this value is true then range becomes limited from left, otherwise becomes unlimited
	 */
	public void setMinUnlimited(boolean minUnlimited) {
		this.minUnlimited = minUnlimited;
	}

	/**
	 * Test if range is unlimited from right
	 * @return true if range is unlimited from right, otherwise false
	 */
	public boolean isMaxUnlimited() {
		return maxUnlimited;
	}

	/**
	 * @param maxUnlimited if this value is true then range becomes limited from right, otherwise becomes unlimited
	 */
	public void setMaxUnlimited(boolean maxUnlimited) {
		this.maxUnlimited = maxUnlimited;
	}

	/**
	 * Normalizes the range (right bound becomes greater or equal to left)
	 */
	public void normalize() {
		if (!minUnlimited && !maxUnlimited) {
			if (min > max) {
				int temp = min;
				min = max;
				max = temp;
			}
		}
	}

	/**
	 * Returns true if specified value is in the range (inclusive), otherwise false
	 * @param value value to be tested
	 * @return true if value is in the range
	 */
	public boolean isInRangeInclusive(int value) {
		if (!minUnlimited && (value < min)) {
			return false;
		}
		if (!maxUnlimited && (value > max)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns special value which becomes the limit when one of bounds is unlimited
	 * @return the unlimitedValue
	 */
	public int getUnlimitedValue() {
		return unlimitedValue;
	}

	/**
	 * Sets special value which becomes the limit when one of bounds is unlimited
	 * @param unlimitedValue the unlimitedValue to set
	 */
	public void setUnlimitedValue(int unlimitedValue) {
		this.unlimitedValue = unlimitedValue;
	}

}
