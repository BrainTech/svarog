package org.signalml.app.view.document.opensignal;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.SignalSource;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.presets.PresetableView;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.document.opensignal.elements.ChannelSelectWithGainEditionPanel;
import org.signalml.app.view.document.opensignal.elements.OtherSettingsPanel;
import org.signalml.app.view.document.opensignal.elements.PresetSelectionPanel;
import org.signalml.app.view.document.opensignal.elements.SignalParametersPanel;
import org.signalml.app.view.document.opensignal.elements.SignalSourceTabbedPane;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.plugin.export.SignalMLException;

public class OpenSignalWizardStepOnePanel extends JPanel implements ChangeListener, PropertyChangeListener, PresetableView {
	protected static final Logger log = Logger.getLogger(OpenSignalWizardStepOnePanel.class);

	private SignalSourceTabbedPane signalSourceTabbedPane;
	private ViewerElementManager viewerElementManager;

	private SignalParametersPanel signalParametersPanel;
	private ChannelSelectWithGainEditionPanel channelSelectPanel;

	private OtherSettingsPanel otherSettingsPanel;
	private PresetSelectionPanel presetSelectionPanel;

	private AbstractOpenSignalDescriptor openSignalDescriptor;

	private final String selectedSourceTab;

	public OpenSignalWizardStepOnePanel(ViewerElementManager viewerElementManager, String selectedSourceTab) {
		this.viewerElementManager = viewerElementManager;
		this.selectedSourceTab = selectedSourceTab;

		this.setLayout(new GridLayout(1, 2));
		this.add(createLeftPanel());
		this.add(createRightPanel());

		preparePanelsForSignalSource();
	}

	protected JPanel createLeftPanel() {
		JPanel leftPanel = new JPanel(new BorderLayout());

		leftPanel.add(getSignalSourceTabbedPane(), BorderLayout.CENTER);
		signalParametersPanel = new SignalParametersPanel();
		signalParametersPanel.addPropertyChangeListener(this);
		leftPanel.add(signalParametersPanel, BorderLayout.SOUTH);

		return leftPanel;
	}

	protected JPanel createRightPanel() {
		JPanel rightPanel = new JPanel(new BorderLayout());

		channelSelectPanel = new ChannelSelectWithGainEditionPanel();
		rightPanel.add(channelSelectPanel, BorderLayout.CENTER);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
		southPanel.add(getOtherSettingsPanel());
		southPanel.add(getPresetSelectionPanel());

		rightPanel.add(southPanel, BorderLayout.SOUTH);

		return rightPanel;
	}

	public OtherSettingsPanel getOtherSettingsPanel() {
		if (otherSettingsPanel == null) {
			otherSettingsPanel = new OtherSettingsPanel(viewerElementManager);
		}
		return otherSettingsPanel;
	}

	protected SignalSourceTabbedPane getSignalSourceTabbedPane() {
		if (signalSourceTabbedPane == null) {
			signalSourceTabbedPane = new SignalSourceTabbedPane(viewerElementManager, selectedSourceTab);
			signalSourceTabbedPane.addChangeListener(this);
			signalSourceTabbedPane.addPropertyChangeListener(this);
			getOtherSettingsPanel().getFileTypeComboBox().addItemListener(signalSourceTabbedPane);
			getOtherSettingsPanel().getRegisterCodecAction().addPropertyChangeListener(signalSourceTabbedPane);
		}
		return signalSourceTabbedPane;
	}

	public PresetSelectionPanel getPresetSelectionPanel() {
		if (presetSelectionPanel == null)
			presetSelectionPanel = new PresetSelectionPanel(this);
		return presetSelectionPanel;
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == signalSourceTabbedPane) {
			preparePanelsForSignalSource();
			fillPanelFromModel(signalSourceTabbedPane.getOpenSignalDescriptor());
		}
	}

	protected void preparePanelsForSignalSource() {
		SignalSource selectedSignalSource = signalSourceTabbedPane.getSelectedSignalSource();
		channelSelectPanel.preparePanelForSignalSource(selectedSignalSource);
		getOtherSettingsPanel().preparePanelForSignalSource(selectedSignalSource);
		getPresetSelectionPanel().preparePanelsForSignalSource(selectedSignalSource);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (SignalSourceTabbedPane.OPEN_SIGNAL_DESCRIPTOR_PROPERTY.equals(evt.getPropertyName())) {
			openSignalDescriptor = (AbstractOpenSignalDescriptor) evt.getNewValue();
			fillPanelFromModel(openSignalDescriptor);
			getPresetSelectionPanel().resetSelectedPreset();
		}
		else if (SignalParametersPanel.NUMBER_OF_CHANNELS_PROPERTY.equals(evt.getPropertyName())) {
			Integer channelCount = (Integer) evt.getNewValue();

			if (!openSignalDescriptor.isCorrectlyRead()
					|| channelCount != openSignalDescriptor.getSignalParameters().getChannelCount()) {

				int i = 0;
				String[] channelLabels = new String[channelCount];
				float[] calibrationGain = new float[channelCount];
				float[] calibrationOffset = new float[channelCount];

				if (openSignalDescriptor.getChannelLabels() != null) {
					int min = Math.min(openSignalDescriptor.getChannelLabels().length, channelCount);
					for (i = 0; i < min; i++) {
						channelLabels[i] = openSignalDescriptor.getChannelLabels()[i];
						calibrationGain[i] = openSignalDescriptor.getSignalParameters().getCalibrationGain()[i];
						calibrationOffset[i] = openSignalDescriptor.getSignalParameters().getCalibrationOffset()[i];
					}
				}

				for (; i < channelCount; i++) {
					channelLabels[i] = "L" + i;
					calibrationGain[i] = 1.0F;
					calibrationOffset[i] = 0.0F;
				}
				openSignalDescriptor.setChannelLabels(channelLabels);
				openSignalDescriptor.getSignalParameters().setCalibrationGain(calibrationGain);
				openSignalDescriptor.getSignalParameters().setCalibrationOffset(calibrationOffset);

				openSignalDescriptor.getSignalParameters().setChannelCount(channelCount);
				channelSelectPanel.fillPanelFromModel(openSignalDescriptor);
			}
		}

	}

	public void fillPanelFromModel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		signalParametersPanel.fillPanelFromModel(openSignalDescriptor);
		channelSelectPanel.fillPanelFromModel(openSignalDescriptor);
		otherSettingsPanel.fillPanelFromModel(openSignalDescriptor);
		presetSelectionPanel.fillPanelFromModel(openSignalDescriptor);
	}

	public void fillModelFromPanel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		signalParametersPanel.fillModelFromPanel(openSignalDescriptor);
		channelSelectPanel.fillModelFromPanel(openSignalDescriptor);
		otherSettingsPanel.fillModelFromPanel(openSignalDescriptor);
	}

	public AbstractOpenSignalDescriptor getOpenSignalDescriptor() {
		if (openSignalDescriptor != null) {
			fillModelFromPanel(openSignalDescriptor);
		}

		return openSignalDescriptor;
	}

	protected void onDialogCloseWithOK() {
		getSignalSourceTabbedPane().onDialogCloseWithOK();
	}

	@Override
	public Preset getPreset() throws SignalMLException {
		ExperimentDescriptor experimentDescriptor = new ExperimentDescriptor((ExperimentDescriptor) getOpenSignalDescriptor());
		fillModelFromPanel(experimentDescriptor);
		return experimentDescriptor;
	}

	@Override
	public void setPreset(Preset preset) throws SignalMLException {
		ExperimentDescriptor experimentDescriptor = (ExperimentDescriptor) preset;
		ExperimentDescriptor currentExperiment = ((ExperimentDescriptor) getOpenSignalDescriptor());
		currentExperiment.copyFromPreset(experimentDescriptor);
		fillPanelFromModel(currentExperiment);
	}

	@Override
	public boolean isPresetCompatible(Preset preset) {
		if (!(preset instanceof ExperimentDescriptor))
			return false;

		ExperimentDescriptor newExperimentDescriptor = (ExperimentDescriptor) preset;
		ExperimentDescriptor currentExperimentDescriptor = (ExperimentDescriptor) openSignalDescriptor;

		int newNumberOfChannels = newExperimentDescriptor.getAmplifier().getChannels().size();
		int currentNumberOfChannels = currentExperimentDescriptor.getAmplifier().getChannels().size();

		if (newNumberOfChannels != currentNumberOfChannels) {
			Dialogs.showError(_("This preset is not compatible with the current experiment—different number of channels."));
			return false;
		}

		return true;
	}

}
