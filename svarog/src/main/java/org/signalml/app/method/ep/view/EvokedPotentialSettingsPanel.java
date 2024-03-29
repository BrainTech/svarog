package org.signalml.app.method.ep.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.method.ep.view.tags.AveragedTagSelectionPanel;
import org.signalml.app.method.ep.view.time.BaselineSelectionPanel;
import org.signalml.app.method.ep.view.time.EvokedPotentialsTimeSelectionPanel;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.method.ep.EvokedPotentialParameters;
import org.signalml.plugin.export.view.FileChooser;

/**
 * This panel consists of various panel which can be used to
 * changed the settings of evoked potentials tool.
 *
 * @author Piotr Szachewicz
 */
public class EvokedPotentialSettingsPanel extends AbstractPanel {

	private AveragedTagSelectionPanel averageedTagSelectionPanel;
	private EvokedPotentialsTimeSelectionPanel averagedTimeSelectionPanel;
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

		controlsPanel.add(getAveragedTimeSelectionPanel());
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

	public EvokedPotentialsTimeSelectionPanel getAveragedTimeSelectionPanel() {
		if (averagedTimeSelectionPanel == null)
			averagedTimeSelectionPanel = new EvokedPotentialsTimeSelectionPanel();
		return averagedTimeSelectionPanel;
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

	public void fillPanelFromModel(EvokedPotentialParameters parameters) {
		getAveragedTagSelectionPanel().fillPanelFromModel(parameters);
		getAveragedTimeSelectionPanel().fillPanelFromModel(parameters);
		getBaselineSelectionPanel().fillPanelFromModel(parameters);
		getFilterPanel().fillPanelFromModel(parameters);
	}

	public void fillModelFromPanel(EvokedPotentialParameters parameters) {

		getAveragedTagSelectionPanel().fillModelFromPanel(parameters);
		getAveragedTimeSelectionPanel().fillModelFromPanel(parameters);
		getBaselineSelectionPanel().fillModelFromPanel(parameters);
		getFilterPanel().fillModelFromPanel(parameters);

	}

	@Override
	public void validatePanel(ValidationErrors errors) {
		getAveragedTagSelectionPanel().validatePanel(errors);
		getAveragedTimeSelectionPanel().validatePanel(errors);
		getBaselineSelectionPanel().validatePanel(errors);
		getFilterPanel().validatePanel(errors);
	}

	public void setTagDocument(TagDocument tagDocument) {
		getAveragedTagSelectionPanel().setTagDocument(tagDocument);
	}

	public void setSignalDocument(SignalDocument signalDocument) {
		getFilterPanel().setSamplingFrequency(signalDocument.getSamplingFrequency());
	}

	public void setFileChooser(FileChooser fileChooser) {
		getFilterPanel().setFileChooser(fileChooser);
	}

}