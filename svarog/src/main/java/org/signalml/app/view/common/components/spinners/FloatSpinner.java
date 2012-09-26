/* Spinner.java created 2011-02-15
 *
 */

package org.signalml.app.view.common.components.spinners;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

		/*
		 * This change listener is responsible for taking the
		 * current float value of this spinner, convert it to
		 * double and then set this value for this spinner.
		 * SpinnerNumberModel doesn't support float values
		 * so in the background this spinner operates on double.
		 */
		this.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				double value = getValue();
				setValue(value);
			}
		});
	}

	@Override
	public Float getValue() {
		return ((Number) super.getValue()).floatValue();
	}

}
