package org.signalml.app.method.ep.view;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.method.ep.EvokedPotentialParameters;

public class FilterPanel extends AbstractPanel implements ActionListener {

	private JCheckBox enableFilteringCheckbox;
	private FilterParametersPanel filterParametersPanel;

	public FilterPanel() {
		super(_("Low pass filtering"));

		createInterface();
	}

	protected void createInterface() {
		setLayout(new BorderLayout());
		add(getEnableFilteringCheckbox(), BorderLayout.NORTH);
		add(getFilterParametersPanel(), BorderLayout.CENTER);
	}

	public JCheckBox getEnableFilteringCheckbox() {
		if (enableFilteringCheckbox == null) {
			enableFilteringCheckbox = new JCheckBox(_("enable filtering"));

			enableFilteringCheckbox.addActionListener(this);
		}
		return enableFilteringCheckbox;
	}

	protected FilterParametersPanel getFilterParametersPanel() {
		if (filterParametersPanel == null) {
			filterParametersPanel = new FilterParametersPanel();
			filterParametersPanel.setEnabledAll(false);
		}
		return filterParametersPanel;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean filteringEnabled = getEnableFilteringCheckbox().isSelected();

		getFilterParametersPanel().setEnabledAll(filteringEnabled);
	}

	public void fillModelFromPanel(EvokedPotentialParameters parameters) {
		parameters.setFilteringEnabled(getEnableFilteringCheckbox().isSelected());
		getFilterParametersPanel().fillModelFromPanel(parameters);
	}

}
