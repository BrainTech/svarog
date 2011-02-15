/* Spinner.java created 2011-02-15
 *
 */

package org.signalml.app.view.montage.filters;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Piotr Szachewicz
 */
public class FloatSpinner extends JSpinner {

	public FloatSpinner(SpinnerNumberModel model) {
		super(model);
	}

	@Override
	public Float getValue() {
		return ((Number) super.getValue()).floatValue();
	}
}
