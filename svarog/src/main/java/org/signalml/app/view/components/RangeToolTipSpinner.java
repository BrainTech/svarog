/* RangeToolTipSpinner.java created 2007-10-05
 *
 */

package org.signalml.app.view.components;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.app.model.components.BoundedSpinnerModel;

/**
 * Spinner which contains the tool-tip if its model is bounded.
 * <p>
 * The tool-tip contains the range (minimum and maximum divided by comma)
 * that is allowed in this spinner.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RangeToolTipSpinner extends JSpinner implements ChangeListener {

	private static final long serialVersionUID = 1L;

	/**
     * Constructs a spinner with an <code>Integer SpinnerNumberModel</code>
     * with initial value 0 and no minimum or maximum limits.
     * <p>
     * Adds the created spinner as the listener to this spinner in order to
     * update tool-tips.
     */
	public RangeToolTipSpinner() {
		super();
		// listen to ourselves in order to update tooltips
		addChangeListener(this);
	}

	/**
     * Constructs a complete spinner with pair of next/previous buttons
     * and an editor for the <code>SpinnerModel</code>.
     * <p>
     * Adds the created spinner as the listener to this spinner in order to
     * update tool-tips.
	 * @param model the model for the spinner
     */
	public RangeToolTipSpinner(SpinnerModel model) {
		super(model);
		// listen to ourselves in order to update tooltips
		addChangeListener(this);
		fireStateChanged();
	}

	/**
	 * Updates the tool-tip, when the change in the model occurred.
	 * <p>
	 * The tool-tip contains the range (minimum and maximum divided by comma)
	 * that is allowed in this spinner.
	 * If the model is no bounded the tool-tip is turned off.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		SpinnerModel model = getModel();
		if (model != null && (model instanceof BoundedSpinnerModel)) {
			BoundedSpinnerModel brm = (BoundedSpinnerModel) model;
			String toolTip = brm.getMinimum().toString() + " - " + brm.getMaximum().toString();
			setToolTipText(toolTip);
		} else {
			setToolTipText(null);
		}
	}

	/**
	 * Removes this class from the list of listeners of the old model and
	 * {@link JSpinner#setModel(SpinnerModel) changes} the model of this
	 * spinner (changing the model adds old listeners to the new model).
	 */
	@Override
	public void setModel(SpinnerModel model) {
		SpinnerModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeChangeListener(this);
		}
		super.setModel(model);
		fireStateChanged();
	}

}
