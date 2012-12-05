package org.signalml.app.view.tag.synchronize;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.app.view.common.components.spinners.FloatSpinner;

public class LengthThresholdValuePanel extends AbstractPanel {

	private FloatSpinner lengthThresholdValueSpinner;

	public LengthThresholdValuePanel() {
		JLabel label = new JLabel(_("Length threshold value [s]"));

		setLayout(new GridLayout(1, 2, 20, 20));
		add(label);
		add(getLengthThresholdValueSpinner());
	}

	public FloatSpinner getLengthThresholdValueSpinner() {
		if (lengthThresholdValueSpinner == null) {
			SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(0.001, 0.0, 50.0, 0.001);
			lengthThresholdValueSpinner = new FloatSpinner(spinnerNumberModel);
		}
		return lengthThresholdValueSpinner;
	}

}
