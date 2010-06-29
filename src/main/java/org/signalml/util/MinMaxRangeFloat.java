/* MinMaxRangeFloat.java created 2008-02-25
 *
 */

package org.signalml.util;

import java.io.Serializable;

/** MinMaxRangeFloat
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MinMaxRangeFloat implements Serializable {

	private float unlimitedValue;

	private static final long serialVersionUID = 1L;

	private float min;
	private float max;

	private boolean minUnlimited;
	private boolean maxUnlimited;

	public MinMaxRangeFloat(float unlimitedValue) {
		this.unlimitedValue = unlimitedValue;
	}

	public MinMaxRangeFloat(float unlimitedValue, float min, float max, boolean minUnlimited, boolean maxUnlimited) {
		this.unlimitedValue = unlimitedValue;
		this.min = min;
		this.max = max;
		this.minUnlimited = minUnlimited;
		this.maxUnlimited = maxUnlimited;
	}

	public MinMaxRangeFloat(float unlimitedValue, boolean unlimited) {
		this.unlimitedValue = unlimitedValue;
		if (unlimited) {
			minUnlimited = true;
			maxUnlimited = true;
		}
	}

	public MinMaxRangeFloat(MinMaxRangeFloat template) {
		this.unlimitedValue = template.unlimitedValue;
		this.min = template.min;
		this.max = template.max;
		this.minUnlimited = template.minUnlimited;
		this.maxUnlimited = template.maxUnlimited;
	}

	public float getMin() {
		return min;
	}

	public float getMinWithUnlimited() {
		return (minUnlimited ? unlimitedValue : min);
	}

	public void setMin(float min) {
		this.min = min;
	}

	public void setMinWithUnlimited(float min) {
		if (min < 0) {
			minUnlimited = true;
		} else {
			minUnlimited = false;
			this.min = min;
		}
	}

	public float getMax() {
		return max;
	}

	public float getMaxWithUnlimited() {
		return (maxUnlimited ? unlimitedValue : max);
	}

	public void setMax(float max) {
		this.max = max;
	}

	public void setMaxWithUnlimited(float max) {
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
				float temp = min;
				min = max;
				max = temp;
			}
		}
	}

	public boolean isInRangeInclusive(float value) {
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
	public float getUnlimitedValue() {
		return unlimitedValue;
	}

	/**
	 * @param unlimitedValue the unlimitedValue to set
	 */
	public void setUnlimitedValue(float unlimitedValue) {
		this.unlimitedValue = unlimitedValue;
	}

}
