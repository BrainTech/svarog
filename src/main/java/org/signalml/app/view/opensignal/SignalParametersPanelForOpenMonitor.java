/* NewClass.java created 2011-03-14
 *
 */

package org.signalml.app.view.opensignal;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class SignalParametersPanelForOpenMonitor extends AbstractSignalParametersPanel {

	public SignalParametersPanelForOpenMonitor(MessageSourceAccessor messageSource, ApplicationConfiguration applicationConfiguration) {
		super(messageSource, applicationConfiguration);
		getSamplingFrequencyComboBox().setEditable(true);

		setEnabledAll(true);
		getSamplingFrequencyComboBox().setEnabled(false);
		getChannelCountSpinner().setEnabled(false);
		getByteOrderComboBox().setEnabled(false);
		getSampleTypeComboBox().setEnabled(false);
        }

	public void fillPanelFromModel(OpenMonitorDescriptor descriptor) {
		getSamplingFrequencyComboBox().setSelectedItem(descriptor.getSamplingFrequency());
		getChannelCountSpinner().setValue(descriptor.getChannelCount());
		getByteOrderComboBox().setSelectedItem(descriptor.getByteOrder());
		getSampleTypeComboBox().setSelectedItem(descriptor.getSampleType());
		getPageSizeSpinner().setValue(descriptor.getPageSize());
		//getBlocksPerPageSpinner().setValue(4);
	}

	public void fillModelFromPanel(OpenMonitorDescriptor descriptor) {
		descriptor.setSamplingFrequency(Float.parseFloat(getSamplingFrequencyComboBox().getSelectedItem().toString()));
		int channelCount = getChannelCountSpinner().getValue();
		descriptor.setChannelCount(channelCount);
		descriptor.setByteOrder((RawSignalByteOrder) getByteOrderComboBox().getSelectedItem());
		descriptor.setSampleType((RawSignalSampleType) getSampleTypeComboBox().getSelectedItem());
		descriptor.setPageSize(getPageSizeSpinner().getValue());
		//descriptor.setBlocksPerPage(getBlocksPerPageSpinner().getValue());

		/* gains and offset are not changed yet - should be connected
		 * to the edit gain&offset dialog
		 */
		float[] gains = new float[channelCount];
		Arrays.fill(gains, 1.0F);
		descriptor.setCalibrationGain(gains);
		float[] offsets = new float[channelCount];
		Arrays.fill(offsets, 0.0F);
		descriptor.setCalibrationOffset(offsets);

		/**
		 * all channels are selected channels
		*/

		try {	
			descriptor.setSelectedChannelList(descriptor.getChannelLabels());
		} catch (Exception ex) {
			Logger.getLogger(SignalParametersPanelForOpenMonitor.class.getName()).log(Level.SEVERE, null, ex);
		}

		int[] selectedChannelsIndices = new int[channelCount];
		for(int i = 0; i < selectedChannelsIndices.length; i++)
			selectedChannelsIndices[i] = i;
	}
        @Override
        protected void fillCurrentModelFromPanel() throws SignalMLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }
}
