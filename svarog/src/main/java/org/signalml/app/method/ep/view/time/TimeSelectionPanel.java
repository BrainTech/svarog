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

	protected FloatSpinner secondsBeforeSpinner;
	protected FloatSpinner secondsAfterSpinner;

	public TimeSelectionPanel(String label) {
		super(label);
	}

	protected FloatSpinner getStartTimeSpinner() {
		if (secondsBeforeSpinner == null) {
			secondsBeforeSpinner = new FloatSpinner(new SpinnerNumberModel(1.0, -100.0, 100.0, 0.1));
		}
		return secondsBeforeSpinner;
	}

	protected FloatSpinner getEndTimeSpinner() {
		if (secondsAfterSpinner == null) {
			secondsAfterSpinner = new FloatSpinner(new SpinnerNumberModel(2.0, -100.0, 100.0, 0.1));
		}
		return secondsAfterSpinner;
	}

	@Override
	protected List<ComponentWithLabel> createComponents() {
		List<ComponentWithLabel> components = new ArrayList<ComponentWithLabel>();

		components.add(new ComponentWithLabel(new JLabel(_("Seconds before [sec]")), getStartTimeSpinner()));
		components.add(new ComponentWithLabel(new JLabel(_("Seconds after [sec]")), getEndTimeSpinner()));

		return components;
	}

	@Override
	protected int getNumberOfColumns() {
		return 1;
	}

}
