/* SignalParametersPanelForRawSignalFile.java created 2011-03-11
 *
 */

package org.signalml.app.view.opensignal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class SignalParametersPanelForRawSignalFile extends AbstractSignalParametersPanel {

	public SignalParametersPanelForRawSignalFile(MessageSourceAccessor messageSource, ApplicationConfiguration applicationConfiguration) {
		super(messageSource, applicationConfiguration);
		setEnabledAll(true);
		getSamplingFrequencyComboBox().setEditable(true);
	}

	@Override
	protected JPanel createButtonPanel() {
		JPanel buttonPanel = super.createButtonPanel();
		buttonPanel.add(new JButton(new ReadXMLManifestAction(messageSource, this)));
		return buttonPanel;
	}

	public void fillPanelFromModel(RawSignalDescriptor descriptor) {
		getSamplingFrequencyComboBox().setSelectedItem(descriptor.getSamplingFrequency());
		getChannelCountSpinner().setValue(descriptor.getChannelCount());
		getByteOrderComboBox().setSelectedItem(descriptor.getByteOrder());
		getSampleTypeComboBox().setSelectedItem(descriptor.getSampleType());
		getPageSizeSpinner().setValue(descriptor.getPageSize());
		getBlocksPerPageSpinner().setValue(descriptor.getBlocksPerPage());

		String[] channelLabels = descriptor.getChannelLabels();
		if (channelLabels != null)
			firePropertyChange(AbstractSignalParametersPanel.CHANNEL_LABELS_PROPERTY, null, channelLabels);
	}

	public void fillModelFromPanel(RawSignalDescriptor descriptor) {
		descriptor.setSamplingFrequency(Float.parseFloat(getSamplingFrequencyComboBox().getSelectedItem().toString()));
		descriptor.setChannelCount(getChannelCountSpinner().getValue());
		descriptor.setByteOrder((RawSignalByteOrder) getByteOrderComboBox().getSelectedItem());
		descriptor.setSampleType((RawSignalSampleType) getSampleTypeComboBox().getSelectedItem());
		descriptor.setPageSize(getPageSizeSpinner().getValue());
		descriptor.setBlocksPerPage(getBlocksPerPageSpinner().getValue());
		descriptor.setSourceSignalType(RawSignalDescriptor.SourceSignalType.RAW);

		descriptor.setCalibrationGain(1.0F);
		descriptor.setCalibrationOffset(0.0F);

	}

        @Override
        protected void fillCurrentModelFromPanel() throws SignalMLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

}
