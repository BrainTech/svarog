package org.signalml.app.model.components;

import javax.swing.DefaultBoundedRangeModel;

/**
 * A bounded model for a slider, with additional functionality of
 * returning values mapped to the exponential scale.
 *
 * @author ptr@mimuw.edu.pl
 */
public class ExpBoundedRangeModel extends DefaultBoundedRangeModel {

	/**
	 * Return selected value, scaled exponentially between minimum and maximum.
	 *
	 * @return current value, scaled exponentially
	 */
	public int getExpValue() {
		double min = getMinimum(), max = getMaximum(), val = super.getValue();
		double exp = Math.exp(
			( (max - val) * Math.log(min) + (val - min) * Math.log(max))
			/ (max - min)
		);
		return (int) Math.round(exp);
	}

	/**
	 * Set a new value for the model with a given value
	 * scaled exponentially between minimum and maximum.
	 *
	 * @param exp new value, scaled exponentially
	 */
	public void setExpValue(int exp) {
		double min = getMinimum(), max = getMaximum();
		if (min <= exp && exp <= max) {
			double lin = min + (max - min) * (Math.log(exp) - Math.log(min)) / (Math.log(max) - Math.log(min));
			super.setValue((int) Math.round(lin));
		}
	}

}
