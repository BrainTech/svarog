/* SignalParametersPanelForOpenMonitor.java created 2011-03-14
 *
 */
package org.signalml.app.view.opensignal;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.plugin.export.SignalMLException;

/**
 * Signal parameters panel adapted to the needs of the open openBCI signal panel.
 *
 * @author Piotr Szachewicz
 */
public class SignalParametersPanelForOpenBCI extends AbstractSignalParametersPanel {

	/**
	 * Constructor.
	 * @param applicationConfiguration the configuration to be used
	 */
	public  SignalParametersPanelForOpenBCI() {
		super();
		getSamplingFrequencyComboBox().setEditable(true);

		setEnabledAll(true);
		getSamplingFrequencyComboBox().setEnabled(false);
		getChannelCountSpinner().setEnabled(false);
		getByteOrderComboBox().setEnabled(false);
		getSampleTypeComboBox().setEnabled(false);
		getEditGainAndOffsetButton().setEnabled(false);
	}

	/**
	 * Fills this panel with the data contained in the descriptor.
	 * @param descriptor the descriptor which should be used to fill this
	 * panel
	 */
	public void fillPanelFromModel(OpenMonitorDescriptor descriptor) {
		getSamplingFrequencyComboBox().setSelectedItem(descriptor.getSamplingFrequency());
		getChannelCountSpinner().setValue(descriptor.getChannelCount());
		getByteOrderComboBox().setSelectedItem(descriptor.getByteOrder());
		getSampleTypeComboBox().setSelectedItem(descriptor.getSampleType());
		getPageSizeSpinner().setValue(descriptor.getPageSize());

		String[] channelLabels = descriptor.getChannelLabels();
		if (channelLabels != null) {
			firePropertyChange(AbstractSignalParametersPanel.CHANNEL_LABELS_PROPERTY, null, channelLabels);
		}

		getEditGainAndOffsetDialog().fillDialogFromModel(descriptor);
		currentModel = descriptor;
	}

	/**
	 * Fills the given descriptor with the data set using this panel.
	 * @param descriptor the descriptor to be changed
	 */
	public void fillModelFromPanel(OpenMonitorDescriptor descriptor) {
		descriptor.setSamplingFrequency(Float.parseFloat(getSamplingFrequencyComboBox().getSelectedItem().toString()));
		int channelCount = getChannelCountSpinner().getValue();
		descriptor.setChannelCount(channelCount);
		descriptor.setByteOrder((RawSignalByteOrder) getByteOrderComboBox().getSelectedItem());
		descriptor.setSampleType((RawSignalSampleType) getSampleTypeComboBox().getSelectedItem());
		descriptor.setPageSize(getPageSizeSpinner().getValue());

		try {
			//all channels are selected channels
			descriptor.setSelectedChannelList(descriptor.getChannelLabels());
		} catch (Exception ex) {
			Logger.getLogger(SignalParametersPanelForOpenBCI.class.getName()).log(Level.SEVERE, null, ex);
		}

		getEditGainAndOffsetDialog().fillModelFromDialog(descriptor);
	}

	@Override
	protected void fillCurrentModelFromPanel() throws SignalMLException {
		fillModelFromPanel((OpenMonitorDescriptor) currentModel);
	}

}
