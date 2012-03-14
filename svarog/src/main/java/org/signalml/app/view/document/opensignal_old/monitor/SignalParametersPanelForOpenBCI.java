package org.signalml.app.view.document.opensignal_old.monitor;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;

import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.document.opensignal.Amplifier;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentStatus;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.SignalParameters;
import org.signalml.app.view.document.opensignal_old.AbstractSignalParametersPanel;
import org.signalml.app.view.document.opensignal_old.elements.AmplifierChannel;
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
			getEditGainAndOffsetButton().setEnabled(false);
			return;
		} else if (descriptor.getStatus() == ExperimentStatus.RUNNING) {
			SignalParameters signalParameters = descriptor.getSignalParameters();

			getSamplingFrequencyComboBox().setSelectedItem(signalParameters.getSamplingFrequency());
			getChannelCountSpinner().setValue(signalParameters.getChannelCount());

			getSamplingFrequencyComboBox().setEnabled(false);
		} else if (descriptor.getStatus() == ExperimentStatus.NEW) {
			Amplifier amplifier = descriptor.getAmplifier();

			getSamplingFrequencyComboBox().setEnabled(true);

			getSamplingFrequencyComboBox().setModel(
					new DefaultComboBoxModel(amplifier
							.getSamplingFrequencies().toArray()));

			getChannelCountSpinner().setEnabled(false);
			getChannelCountSpinner().setValue(
					amplifier.getChannels().size());
		}
		
		getEditGainAndOffsetDialog().fillDialogFromModel(descriptor);
		getEditGainAndOffsetButton().setEnabled(true);

		currentModel = descriptor;
	}

	/**
	 * Fills the given descriptor with the data set using this panel.
	 * 
	 * @param descriptor
	 *            the descriptor to be changed
	 */
	public void fillModelFromPanel(ExperimentDescriptor descriptor) {
		SignalParameters signalParameters = descriptor.getSignalParameters();
		
		signalParameters.setSamplingFrequency(Float
				.parseFloat(getSamplingFrequencyComboBox().getSelectedItem()
						.toString()));
		int channelCount = getChannelCountSpinner().getValue();
		signalParameters.setChannelCount(channelCount);
		signalParameters.setPageSize(getPageSizeSpinner().getValue());

		List<AmplifierChannel> channels = descriptor.getAmplifier().getSelectedChannels();

		float[] gain = new float[channels.size()];
		float[] offset = new float[channels.size()];

		int i = 0;
		for (AmplifierChannel channel: channels) {
			gain[i] = channel.getCalibrationGain();
			offset[i] = channel.getCalibrationOffset();
			i++;
		}
		signalParameters.setCalibrationGain(gain);
		signalParameters.setCalibrationOffset(offset);

		signalParameters.setChannelCount(channels.size());
	}
	
	@Override
	protected void fillCurrentModelFromPanel() throws SignalMLException {
		fillModelFromPanel((ExperimentDescriptor) currentModel);
	}

}
