package org.signalml.app.method.ep.view.time;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

import org.signalml.app.view.common.components.panels.ComponentWithLabel;
import org.signalml.app.view.common.components.panels.LabeledComponentsPanel;
import org.signalml.app.view.common.components.spinners.FloatSpinner;

public class TimeSelectionPanel extends LabeledComponentsPanel {

	protected FloatSpinner startTimeSpinner;
	protected FloatSpinner endTimeSpinner;

	public TimeSelectionPanel(String label) {
		super(label);
	}

	protected FloatSpinner getStartTimeSpinner() {
		if (startTimeSpinner == null) {
			startTimeSpinner = new FloatSpinner(new SpinnerNumberModel(-1.0, -100.0, 100.0, 0.1));
		}
		return startTimeSpinner;
	}

	protected FloatSpinner getEndTimeSpinner() {
		if (endTimeSpinner == null) {
			endTimeSpinner = new FloatSpinner(new SpinnerNumberModel(2.0, -100.0, 100.0, 0.1));
		}
		return endTimeSpinner;
	}

	@Override
	protected List<ComponentWithLabel> createComponents() {
		List<ComponentWithLabel> components = new ArrayList<ComponentWithLabel>();

		components.add(new ComponentWithLabel(new JLabel(_("Start time [sec]")), getStartTimeSpinner()));
		components.add(new ComponentWithLabel(new JLabel(_("End time [sec]")), getEndTimeSpinner()));

		return components;
	}

	@Override
	protected int getNumberOfColumns() {
		return 1;
	}

}
