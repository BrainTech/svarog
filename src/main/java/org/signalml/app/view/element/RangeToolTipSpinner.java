/* RangeToolTipSpinner.java created 2007-10-05
 * 
 */

package org.signalml.app.view.element;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.app.model.BoundedSpinnerModel;

/** RangeToolTipSpinner
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RangeToolTipSpinner extends JSpinner implements ChangeListener {

	private static final long serialVersionUID = 1L;

	public RangeToolTipSpinner() {
		super();
		// listen to ourselves in order to update tooltips
		addChangeListener(this);
	}	
	
	public RangeToolTipSpinner(SpinnerModel model) {
		super(model);
		// listen to ourselves in order to update tooltips
		addChangeListener(this);
		fireStateChanged();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		SpinnerModel model = getModel();
		if( model != null && (model instanceof BoundedSpinnerModel) ) {
			BoundedSpinnerModel brm = (BoundedSpinnerModel) model;
			String toolTip = brm.getMinimum().toString() + " - " + brm.getMaximum().toString();
			setToolTipText(toolTip);
		} else {
			setToolTipText(null);
		}
	}
	
	@Override
	public void setModel(SpinnerModel model) {
		SpinnerModel oldModel = getModel();
		if( oldModel != null ) {
			oldModel.removeChangeListener(this);
		}
		super.setModel(model);
		fireStateChanged();
	}
	
}
