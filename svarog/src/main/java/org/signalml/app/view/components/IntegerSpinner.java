/* IntegerSpinner.java created 2011-03-11
 *
 */

package org.signalml.app.view.components;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * Spinner which more conveniently returns its current value if it
 * is of type: integer.
 *
 * @author Piotr Szachewicz
 */
public class IntegerSpinner extends JSpinner {

	/**
	 * Constructor.
	 * @param model spinner number model to be used with this spinner.
	 */
	public IntegerSpinner(SpinnerNumberModel model) {
		super(model);
	}

	@Override
	public Integer getValue() {
		return ((Number) super.getValue()).intValue();
	}

	/**
	 * Sets the maximum value which can be set using this spinner.
	 * @param maximum the maximum value which can be set using this spinner
	 */
	public void setMaximumValue(double maximum) {
		SpinnerNumberModel model = (SpinnerNumberModel) super.getModel();
		model.setMaximum(maximum);
		this.setModel(model);
	}

}

