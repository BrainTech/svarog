package org.signalml.app.method.ep.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.signalml.app.method.ep.EvokedPotentialApplicationData;
import org.signalml.app.method.ep.view.tags.AveragedTagSelectionPanel;
import org.signalml.app.method.ep.view.time.BaselineSelectionPanel;
import org.signalml.app.method.ep.view.time.EvokedPotentialsTimeSelectionPanel;
import org.signalml.app.view.common.components.panels.AbstractPanel;

public class EvokedPotentialSettingsPanel extends AbstractPanel {

	private AveragedTagSelectionPanel averageedTagSelectionPanel;
	private EvokedPotentialsTimeSelectionPanel timeSelectionPanel;
	private BaselineSelectionPanel baselineSelectionPanel;
	private FilterPanel filterPanel;

	public EvokedPotentialSettingsPanel() {
		createInterface();
	}

	protected void createInterface() {
		setLayout(new GridLayout(1, 2));
		add(createLeftPanel());
		add(createRightPanel());
	}

	protected JPanel createLeftPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getAveragedTagSelectionPanel(), BorderLayout.CENTER);

		return panel;
	}

	protected JPanel createRightPanel() {
		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));

		controlsPanel.add(getTimeSelectionPanel());
		controlsPanel.add(getBaselineSelectionPanel());
		controlsPanel.add(getFilterPanel());

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(controlsPanel, BorderLayout.NORTH);
		panel.add(new JPanel(), BorderLayout.CENTER);

		return panel;
	}

	public AveragedTagSelectionPanel getAveragedTagSelectionPanel() {
		if (averageedTagSelectionPanel == null)
			averageedTagSelectionPanel = new AveragedTagSelectionPanel();
		return averageedTagSelectionPanel;
	}

	public EvokedPotentialsTimeSelectionPanel getTimeSelectionPanel() {
		if (timeSelectionPanel == null)
			timeSelectionPanel = new EvokedPotentialsTimeSelectionPanel();
		return timeSelectionPanel;
	}

	public BaselineSelectionPanel getBaselineSelectionPanel() {
		if (baselineSelectionPanel == null)
			baselineSelectionPanel = new BaselineSelectionPanel();
		return baselineSelectionPanel;
	}

	public FilterPanel getFilterPanel() {
		if (filterPanel == null)
			filterPanel = new FilterPanel();
		return filterPanel;
	}

	public void fillPanelFromModel(EvokedPotentialApplicationData data) {
		getAveragedTagSelectionPanel().fillPanelFromModel(data);
	}

}