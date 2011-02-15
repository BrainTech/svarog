/* Spinner.java created 2011-02-15
 *
 */

package org.signalml.app.view.montage.filters;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * Spinner which more conveniently returns its current value which
 * is of type float.
 *
 * @author Piotr Szachewicz
 */
public class FloatSpinner extends JSpinner {

	/**
	 * Constructor
	 * @param model spinner number model to be used with this spinner.
	 */
	public FloatSpinner(SpinnerNumberModel model) {
		super(model);
	}

	@Override
	public Float getValue() {
		return ((Number) super.getValue()).floatValue();
	}
}
