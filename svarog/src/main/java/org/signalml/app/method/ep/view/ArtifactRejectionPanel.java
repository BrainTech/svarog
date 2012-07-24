package org.signalml.app.method.ep.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.signalml.app.method.ep.EvokedPotentialApplicationData;
import org.signalml.app.method.ep.view.tags.ArtifactTagsSelectionPanel;
import org.signalml.app.view.common.components.panels.AbstractPanel;

public class ArtifactRejectionPanel extends AbstractPanel {

	private ArtifactTagsSelectionPanel artifactTagsSelectionPanel;

	public ArtifactRejectionPanel() {
		super();

		createInterface();
	}

	protected void createInterface() {
		setLayout(new GridLayout(1, 2));
		add(createLeftPanel());
		add(createRightPanel());
	}

	protected JPanel createLeftPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getArtifactTagsSelectionPanel(), BorderLayout.CENTER);
		return panel;
	}

	protected JPanel createRightPanel() {
		return new JPanel();
	}

	public ArtifactTagsSelectionPanel getArtifactTagsSelectionPanel() {
		if (artifactTagsSelectionPanel == null)
			artifactTagsSelectionPanel = new ArtifactTagsSelectionPanel();
		return artifactTagsSelectionPanel;
	}

	public void fillPanelFromModel(EvokedPotentialApplicationData data) {
		getArtifactTagsSelectionPanel().fillPanelFromModel(data);
	}

}
