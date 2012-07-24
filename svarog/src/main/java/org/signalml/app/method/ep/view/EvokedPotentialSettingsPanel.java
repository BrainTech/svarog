package org.signalml.app.method.ep.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.signalml.app.method.ep.EvokedPotentialApplicationData;
import org.signalml.app.method.ep.view.tags.ArtifactTagsSelectionPanel;
import org.signalml.app.method.ep.view.tags.AveragedTagSelectionPanel;
import org.signalml.app.method.ep.view.time.BaselineSelectionPanel;
import org.signalml.app.method.ep.view.time.EvokedPotentialsTimeSelectionPanel;
import org.signalml.app.view.common.components.panels.AbstractPanel;

public class EvokedPotentialSettingsPanel extends AbstractPanel {

	private AveragedTagSelectionPanel averageedTagSelectionPanel;
	private EvokedPotentialsTimeSelectionPanel timeSelectionPanel;
	private BaselineSelectionPanel baselineSelectionPanel;
	private ArtifactTagsSelectionPanel artifactTagsSelectionPanel;
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
		panel.add(getTimeSelectionPanel(), BorderLayout.SOUTH);

		return panel;
	}

	protected JPanel createRightPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getArtifactTagsSelectionPanel(), BorderLayout.CENTER);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
		southPanel.add(getBaselineSelectionPanel());
		southPanel.add(getFilterPanel());

		panel.add(southPanel, BorderLayout.SOUTH);

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

	public ArtifactTagsSelectionPanel getArtifactTagsSelectionPanel() {
		if (artifactTagsSelectionPanel == null)
			artifactTagsSelectionPanel = new ArtifactTagsSelectionPanel();
		return artifactTagsSelectionPanel;
	}

	public FilterPanel getFilterPanel() {
		if (filterPanel == null)
			filterPanel = new FilterPanel();
		return filterPanel;
	}

	public void fillPanelFromModel(EvokedPotentialApplicationData data) {
		getAveragedTagSelectionPanel().fillPanelFromModel(data);
		getArtifactTagsSelectionPanel().fillPanelFromModel(data);
	}

}