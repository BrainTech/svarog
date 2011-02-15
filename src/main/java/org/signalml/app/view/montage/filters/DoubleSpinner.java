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
public class DoubleSpinner extends JSpinner {

	public DoubleSpinner(SpinnerNumberModel model) {
		super(model);
	}

	@Override
	public Double getValue() {
		return ((Number) super.getValue()).doubleValue();
	}
}
