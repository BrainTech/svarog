/* MinMaxRange.java created 2008-02-13
 *
 */

package org.signalml.util;

import java.io.Serializable;

/** MinMaxRange
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MinMaxRange implements Serializable {

	public static final int UNLIMITED = -9999;
	public static final int AUTO = -1111;

	private double unlimitedValue;

	private static final long serialVersionUID = 1L;

	private double min;
	private double max;

	private boolean minUnlimited;
	private boolean maxUnlimited;

	public MinMaxRange(double unlimitedValue) {
		this.unlimitedValue = unlimitedValue;
	}

	public MinMaxRange(double unlimitedValue, double min, double max, boolean minUnlimited, boolean maxUnlimited) {
		this.min = min;
		this.max = max;
		this.minUnlimited = minUnlimited;
		this.maxUnlimited = maxUnlimited;
	}

	public MinMaxRange(double unlimitedValue, boolean unlimited) {
		this.unlimitedValue = unlimitedValue;
		if (unlimited) {
			minUnlimited = true;
			maxUnlimited = true;
		}
	}

	public MinMaxRange(MinMaxRange template) {
		this.unlimitedValue = template.unlimitedValue;
		this.min = template.min;
		this.max = template.max;
		this.minUnlimited = template.minUnlimited;
		this.maxUnlimited = template.maxUnlimited;
	}

	public double getMin() {
		return min;
	}

	public double getMinWithUnlimited() {
		return (minUnlimited ? unlimitedValue : min);
	}

	public void setMin(double min) {
		this.min = min;
	}

	public void setMinWithUnlimited(double min) {
		if (min < 0) {
			minUnlimited = true;
		} else {
			minUnlimited = false;
			this.min = min;
		}
	}

	public double getMax() {
		return max;
	}

	public double getMaxWithUnlimited() {
		return (maxUnlimited ? unlimitedValue : max);
	}

	public void setMax(double max) {
		this.max = max;
	}

	public void setMaxWithUnlimited(double max) {
		if (max < 0) {
			maxUnlimited = true;
		} else {
			maxUnlimited = false;
			this.max = max;
		}
	}

	public boolean isMinUnlimited() {
		return minUnlimited;
	}

	public void setMinUnlimited(boolean minUnlimited) {
		this.minUnlimited = minUnlimited;
	}

	public boolean isMaxUnlimited() {
		return maxUnlimited;
	}

	public void setMaxUnlimited(boolean maxUnlimited) {
		this.maxUnlimited = maxUnlimited;
	}

	public void normalize() {
		if (!minUnlimited && !maxUnlimited) {
			if (min > max) {
				double temp = min;
				min = max;
				max = temp;
			}
		}
	}

	public boolean isInRangeInclusive(double value) {
		if (!minUnlimited && (value < min)) {
			return false;
		}
		if (!maxUnlimited && (value > max)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the unlimitedValue
	 */
	public double getUnlimitedValue() {
		return unlimitedValue;
	}

	/**
	 * @param unlimitedValue the unlimitedValue to set
	 */
	public void setUnlimitedValue(double unlimitedValue) {
		this.unlimitedValue = unlimitedValue;
	}

}
