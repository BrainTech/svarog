package org.signalml.app.method.ep.view.time;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.app.method.ep.EvokedPotentialApplicationData;
import org.signalml.method.ep.EvokedPotentialParameters;

public class EvokedPotentialsTimeSelectionPanel extends TimeSelectionPanel {

	public EvokedPotentialsTimeSelectionPanel() {
		super(_("Averaging time"));
	}

	public void fillModelFromPanel(EvokedPotentialParameters parameters) {
		parameters.setAveragingTimeBefore(getStartTimeSpinner().getValue());
		parameters.setAveragingTimeAfter(getEndTimeSpinner().getValue());
	}

	public void fillPanelFromModel(EvokedPotentialApplicationData data) {
		EvokedPotentialParameters parameters = data.getParameters();

		getStartTimeSpinner().setValue(parameters.getAveragingTimeBefore());
		getEndTimeSpinner().setValue(parameters.getAveragingTimeAfter());
	}

}
