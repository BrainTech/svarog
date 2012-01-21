package org.signalml.app.view.document.opensignal.monitor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;

import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.document.opensignal.Amplifier;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentStatus;
import org.signalml.app.model.document.opensignal.OpenMonitorDescriptor;
import org.signalml.app.model.document.opensignal.SignalParameters;
import org.signalml.app.view.document.opensignal.AbstractSignalParametersPanel;
import org.signalml.plugin.export.SignalMLException;

/**
 * Signal parameters panel adapted to the needs of the open openBCI signal
 * panel.
 * 
 * @author Piotr Szachewicz
 */
public class SignalParametersPanelForOpenBCI extends AbstractSignalParametersPanel {

	/**
	 * Constructor.
	 * 
	 * @param applicationConfiguration
	 *            the configuration to be used
	 */
	public SignalParametersPanelForOpenBCI() {
		super();
		setEnabledAll(true);
		getSamplingFrequencyComboBox().setEnabled(false);
		getChannelCountSpinner().setEnabled(false);
		getByteOrderComboBox().setEnabled(false);
		getSampleTypeComboBox().setEnabled(false);
		getEditGainAndOffsetButton().setEnabled(false);
	}

	/**
	 * Fills this panel with the data contained in the descriptor.
	 * 
	 * @param descriptor
	 *            the descriptor which should be used to fill this panel
	 */
	public void fillPanelFromModel(ExperimentDescriptor descriptor) {

		if (descriptor == null) {
			getSamplingFrequencyComboBox().setEnabled(false);
		} else if (descriptor.getExperimentStatus() == ExperimentStatus.RUNNING) {
			SignalParameters signalParameters = descriptor.getSignalParameters();

			getSamplingFrequencyComboBox().setSelectedItem(signalParameters.getSamplingFrequency());
			getChannelCountSpinner().setValue(signalParameters.getChannelCount());

			getSamplingFrequencyComboBox().setEnabled(false);
		} else if (descriptor.getExperimentStatus() == ExperimentStatus.NEW) {
			Amplifier amplifier = descriptor.getAmplifier();

			getSamplingFrequencyComboBox().setEnabled(true);

			getSamplingFrequencyComboBox().setModel(
					new DefaultComboBoxModel(amplifier
							.getAvailableSamplingFrequencies().toArray()));

			getChannelCountSpinner().setEnabled(false);
			getChannelCountSpinner().setValue(
					amplifier.getChannels().getNumberOfChannels());

			// getEditGainAndOffsetDialog().fillDialogFromModel(descriptor);
		}

		/*
		 * String[] channelLabels = descriptor.getChannelLabels(); if
		 * (channelLabels != null) {
		 * firePropertyChange(AbstractSignalParametersPanel
		 * .CHANNEL_LABELS_PROPERTY, null, channelLabels); }
		 */

		// getEditGainAndOffsetDialog().fillDialogFromModel(descriptor);
		currentModel = descriptor;
	}

	/**
	 * Fills the given descriptor with the data set using this panel.
	 * 
	 * @param descriptor
	 *            the descriptor to be changed
	 */
	public void fillModelFromPanel(OpenMonitorDescriptor descriptor) {
		descriptor.setSamplingFrequency(Float
				.parseFloat(getSamplingFrequencyComboBox().getSelectedItem()
						.toString()));
		int channelCount = getChannelCountSpinner().getValue();
		descriptor.setChannelCount(channelCount);
		descriptor.setByteOrder((RawSignalByteOrder) getByteOrderComboBox().getSelectedItem());
		descriptor.setSampleType((RawSignalSampleType) getSampleTypeComboBox().getSelectedItem());
		descriptor.setPageSize(getPageSizeSpinner().getValue());

		try {
			// all channels are selected channels
			descriptor.setSelectedChannelList(descriptor.getChannelLabels());
		} catch (Exception ex) {
			Logger.getLogger(SignalParametersPanelForOpenBCI.class.getName()).log(Level.SEVERE, null, ex);
		}

		// getEditGainAndOffsetDialog().fillModelFromDialog(descriptor);
	}

	@Override
	protected void fillCurrentModelFromPanel() throws SignalMLException {
		fillModelFromPanel((OpenMonitorDescriptor) currentModel);
	}

}
