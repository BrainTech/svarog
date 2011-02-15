/* Spinner.java created 2011-02-15
 *
 */

package org.signalml.app.view.montage.filters;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * Spinner which more conveniently returns its current value if it
 * is of type: double.
 *
 * @author Piotr Szachewicz
 */
public class DoubleSpinner extends JSpinner {

	/**
	 * Constructor.
	 * @param model spinner number model to be used with this spinner.
	 */
	public DoubleSpinner(SpinnerNumberModel model) {
		super(model);
	}

	@Override
	public Double getValue() {
		return ((Number) super.getValue()).doubleValue();
	}
}
