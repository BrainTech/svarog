package org.signalml.app.method.ep.view;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionListener;

import org.signalml.app.view.common.components.panels.PanelWithEnablingCheckbox;
import org.signalml.method.ep.EvokedPotentialParameters;

/**
 * This is a panel for enabling/disabling the low-pass filter
 * and selecting its parameters.
 *
 * @author Piotr Szachewicz
 */
public class FilterPanel extends PanelWithEnablingCheckbox<FilterParametersPanel> implements ActionListener {

	public FilterPanel() {
		super(_("EP averaging result filtering"));
	}

	@Override
	protected FilterParametersPanel getPanel() {
		if (panel == null) {
			panel = new FilterParametersPanel();
		}
		return panel;
	}

	@Override
	protected String getEnableCheckboxText() {
		return _("enable filtering");
	}

	public void fillModelFromPanel(EvokedPotentialParameters parameters) {
		parameters.setFilteringEnabled(isCheckboxSelected());
		getPanel().fillModelFromPanel(parameters);
	}

	public void fillPanelFromModel(EvokedPotentialParameters parameters) {
		setCheckboxSelected(parameters.isFilteringEnabled());
		getPanel().fillPanelFromModel(parameters);
	}

	public void setSamplingFrequency(float samplingFrequency) {
		getPanel().setSamplingFrequency(samplingFrequency);
	}

}
