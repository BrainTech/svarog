package org.signalml.app.method.ep.view.time;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.method.ep.EvokedPotentialParameters;

public class BaselineSelectionPanel extends TimeSelectionPanel {

	public BaselineSelectionPanel() {
		super(_("Baseline selection"));
	}

	public void fillModelFromPanel(EvokedPotentialParameters parameters) {
		parameters.setBaselineSelectionStart(getStartTimeSpinner().getValue());
		parameters.setBaselineSelectionEnd(getEndTimeSpinner().getValue());
	}

}
