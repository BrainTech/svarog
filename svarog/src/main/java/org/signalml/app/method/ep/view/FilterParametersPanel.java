package org.signalml.app.method.ep.view;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

import org.signalml.app.view.common.components.panels.ComponentWithLabel;
import org.signalml.app.view.common.components.panels.LabeledComponentsPanel;
import org.signalml.app.view.common.components.spinners.FloatSpinner;
import org.signalml.method.ep.EvokedPotentialParameters;

/**
 * This is a panel for selecting the cut-off frequency of a low pass filter
 * that will be used to filter the result of evoked potentials
 * averaging.
 *
 * @author Piotr Szachewicz
 */
public class FilterParametersPanel extends LabeledComponentsPanel {

	private FloatSpinner cutoffFrequencySpinner;

	public FilterParametersPanel() {
		super("");
	}

	public FloatSpinner getCutoffFrequencySpinner() {
		if (cutoffFrequencySpinner == null)
			cutoffFrequencySpinner = new FloatSpinner(new SpinnerNumberModel(20.0, 0.01, 100.0, 0.1));
		return cutoffFrequencySpinner;
	}

	@Override
	protected List<ComponentWithLabel> createComponents() {
		List<ComponentWithLabel> list = new ArrayList<ComponentWithLabel>();
		list.add(new ComponentWithLabel(new JLabel(_("Cut-off frequency [Hz]")), getCutoffFrequencySpinner()));
		return list;
	}

	@Override
	protected int getNumberOfColumns() {
		return 1;
	}

	public void fillModelFromPanel(EvokedPotentialParameters parameters) {
		parameters.setFilterCutOffFrequency(getCutoffFrequencySpinner().getValue());
	}

	public void fillPanelFromModel(EvokedPotentialParameters parameters) {
		getCutoffFrequencySpinner().setValue(parameters.getFilterCutOffFrequency());
	}

}
