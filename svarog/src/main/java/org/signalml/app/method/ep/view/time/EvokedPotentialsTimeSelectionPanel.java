package org.signalml.app.method.ep.view.time;

import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.method.ep.EvokedPotentialParameters;

/**
 * This is a panel for selecting which parts of the signal should be
 * averaged.
 *
 * @author Piotr Szachewicz
 */
public class EvokedPotentialsTimeSelectionPanel extends TimeSelectionPanel {

	public EvokedPotentialsTimeSelectionPanel() {
		super(_("Averaging time"));
	}

	public void fillModelFromPanel(EvokedPotentialParameters parameters) {
		parameters.setAveragingStartTime(getStartTimeSpinner().getValue());
		parameters.setAveragingTimeLength(getLengthSpinner().getValue());
	}

	public void fillPanelFromModel(EvokedPotentialParameters parameters) {
		getStartTimeSpinner().setValue(parameters.getAveragingStartTime());
		getLengthSpinner().setValue(parameters.getAveragingTimeLength());
	}

}
