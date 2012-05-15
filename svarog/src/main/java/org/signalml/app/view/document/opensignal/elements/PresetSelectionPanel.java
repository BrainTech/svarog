package org.signalml.app.view.document.opensignal.elements;

import java.awt.BorderLayout;

import org.signalml.app.SvarogApplication;
import org.signalml.app.config.preset.ExperimentsSettingsPresetManager;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;
import org.signalml.app.model.document.opensignal.elements.SignalSource;
import org.signalml.app.view.components.AbstractPanel;
import org.signalml.app.view.components.presets.CompactPresetControlsPanel;
import org.signalml.app.view.document.opensignal.OpenSignalWizardStepOnePanel;

public class PresetSelectionPanel extends AbstractPanel {

	private OpenSignalWizardStepOnePanel openSignalWizardStepOnePanel;
	private CompactPresetControlsPanel presetControlsPanel;

	public PresetSelectionPanel(OpenSignalWizardStepOnePanel openSignalWizardStepOnePanel) {
		this.openSignalWizardStepOnePanel = openSignalWizardStepOnePanel;

		this.setLayout(new BorderLayout());
		this.add(getPresetControlsPanel(), BorderLayout.CENTER);
	}

	public void preparePanelsForSignalSource(SignalSource signalSource) {
		presetControlsPanel.setVisible(signalSource == SignalSource.OPENBCI);
	}

	public void fillPanelFromModel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		if (openSignalDescriptor == null)
			getPresetControlsPanel().setEnabledAll(false);

		if (openSignalDescriptor instanceof ExperimentDescriptor) {
			ExperimentDescriptor experimentDescriptor = (ExperimentDescriptor) openSignalDescriptor;

			boolean isExperimentNew = experimentDescriptor.getStatus() == ExperimentStatus.NEW;
			getPresetControlsPanel().setEnabledAll(isExperimentNew);
		}
	}

	public void resetSelectedPreset() {
		getPresetControlsPanel().resetPresetComboBoxSelection();
	}

	public CompactPresetControlsPanel getPresetControlsPanel() {
		if (presetControlsPanel == null) {
			ExperimentsSettingsPresetManager experimentsSettingsPresetManager = SvarogApplication.getSharedInstance().getViewerElementManager().getExperimentsSettingsPresetManager();
			presetControlsPanel = new CompactPresetControlsPanel(experimentsSettingsPresetManager, openSignalWizardStepOnePanel);
		}
		return presetControlsPanel;
	}
}
