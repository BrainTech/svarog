package org.signalml.app.view.document.opensignal;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.elements.SignalSource;
import org.signalml.app.view.document.opensignal.elements.ChannelSelectPanel;
import org.signalml.app.view.document.opensignal.elements.OtherSettingsPanel;
import org.signalml.app.view.document.opensignal.elements.SignalParametersPanel;
import org.signalml.app.view.document.opensignal.elements.SignalSourceTabbedPane;
import org.signalml.app.view.workspace.ViewerElementManager;

public class OpenSignalWizardStepOnePanel extends JPanel implements ChangeListener, PropertyChangeListener {
	protected static final Logger log = Logger.getLogger(OpenSignalWizardStepOnePanel.class);

	private SignalSourceTabbedPane signalSourceTabbedPane;
	private ViewerElementManager viewerElementManager;

	private SignalParametersPanel signalParametersPanel;
	private ChannelSelectPanel channelSelectPanel;

	private OtherSettingsPanel otherSettingsPanel;

	private AbstractOpenSignalDescriptor openSignalDescriptor;

	public OpenSignalWizardStepOnePanel(ViewerElementManager viewerElementManager) {
		this.viewerElementManager = viewerElementManager;

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

		channelSelectPanel = new ChannelSelectPanel();
		rightPanel.add(channelSelectPanel, BorderLayout.CENTER);
		rightPanel.add(getOtherSettingsPanel(), BorderLayout.SOUTH);

		return rightPanel;
	}

	public OtherSettingsPanel getOtherSettingsPanel() {
		if (otherSettingsPanel == null)
			otherSettingsPanel = new OtherSettingsPanel(viewerElementManager);
		return otherSettingsPanel;
	}

	protected SignalSourceTabbedPane getSignalSourceTabbedPane() {
		if (signalSourceTabbedPane == null) {
			signalSourceTabbedPane = new SignalSourceTabbedPane(viewerElementManager);
			signalSourceTabbedPane.addChangeListener(this);
			signalSourceTabbedPane.addPropertyChangeListener(this);
			getOtherSettingsPanel().getFileTypeComboBox().addItemListener(signalSourceTabbedPane);
		}
		return signalSourceTabbedPane;
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
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (SignalSourceTabbedPane.OPEN_SIGNAL_DESCRIPTOR_PROPERTY.equals(evt.getPropertyName())) {
			openSignalDescriptor = (AbstractOpenSignalDescriptor) evt.getNewValue();
			fillPanelFromModel(openSignalDescriptor);
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
	}

	public AbstractOpenSignalDescriptor getOpenSignalDescriptor() {
		if (openSignalDescriptor != null) {
			signalParametersPanel.fillModelFromPanel(openSignalDescriptor);
			channelSelectPanel.fillModelFromPanel(openSignalDescriptor);
			otherSettingsPanel.fillModelFromPanel(openSignalDescriptor);
		}

		return openSignalDescriptor;
	}

	protected void onDialogCloseWithOK() {
		getSignalSourceTabbedPane().onDialogCloseWithOK();
	}
}
