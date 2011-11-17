package org.signalml.app.view.opensignal;

import static org.signalml.app.SvarogI18n._;
import javax.swing.DefaultComboBoxModel;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.AmplifierConnectionDescriptor;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.plugin.export.SignalMLException;

/**
 * A {@link SignalParametersPanel} for amplifier connection.
 *
 * @author Tomasz Sawicki
 */
public class SignalParametersPanelForAmplifierConnection extends AbstractSignalParametersPanel {

	/**
         * Application configuration.
         */
        protected ApplicationConfiguration applicationConfiguration;

        /**
         * Default constructor.
         *
         * @param applicationConfiguration {@link #applicationConfiguration}
         */
        public SignalParametersPanelForAmplifierConnection(ApplicationConfiguration applicationConfiguration) {

                super();
		this.applicationConfiguration = applicationConfiguration;
        }

       /**
         * Fills this panel from an {@link AmplifierConnectionDescriptor} object.
         *
         * @param descriptor the descriptor
         */
        public void fillPanelFromModel(AmplifierConnectionDescriptor descriptor) {

                if (descriptor.getAmplifierInstance() == null) {
                        setEnabledAll(false);
                } else {
                        getSamplingFrequencyComboBox().setEditable(false);
                        getSamplingFrequencyComboBox().setModel(new DefaultComboBoxModel(
                                descriptor.getAmplifierInstance().getDefinition().getAvailableFrequencies().toArray()));
			Float samplingFrequency = descriptor.getOpenMonitorDescriptor().getSamplingFrequency();
			getSamplingFrequencyComboBox().setSelectedItem(samplingFrequency);

                        getChannelCountSpinner().setEnabled(false);
                        getChannelCountSpinner().setValue(descriptor.getOpenMonitorDescriptor().getChannelCount());

                        getByteOrderComboBox().setEnabled(false);
                        getByteOrderComboBox().setSelectedItem(descriptor.getOpenMonitorDescriptor().getByteOrder());
                        
                        getSampleTypeComboBox().setEnabled(false);
                        getByteOrderComboBox().setSelectedItem(descriptor.getOpenMonitorDescriptor().getSampleType());
                        
                        Float pageSize = descriptor.getOpenMonitorDescriptor().getPageSize();
                        if (pageSize == null || pageSize <= 0) pageSize = applicationConfiguration.getPageSize();
                        getPageSizeSpinner().setValue(20f);

                        Integer blocksPerPage = applicationConfiguration.getBlocksPerPage();
                        getBlocksPerPageSpinner().setValue(blocksPerPage);
                        getBlocksPerPageSpinner().setEnabled(false);

                        getEditGainAndOffsetDialog().fillDialogFromModel(descriptor);
                }
                
                currentModel = descriptor;

		String[] channelLabels = descriptor.getOpenMonitorDescriptor().getChannelLabels();
		if (channelLabels != null)
			firePropertyChange(AbstractSignalParametersPanel.CHANNEL_LABELS_PROPERTY, null, channelLabels);
        }

        /**
         * Fills an {@link AmplifierConnectionDescriptor} from this panel.
         *
         * @param descriptor the descriptor
         * @throws SignalMLException when input data is invalid
         */
        public void fillModelFromPanel(AmplifierConnectionDescriptor descriptor) throws SignalMLException {

                Float samplingFrequency;
                Float pageSize;

                try {
                        samplingFrequency = Float.parseFloat(getSamplingFrequencyComboBox().getModel().getSelectedItem().toString());
                } catch (Exception ex) {
                        throw new SignalMLException(_("Invalid data entered"));
                }

                try {
                        pageSize = getPageSizeSpinner().getValue();
                        if (pageSize <= 0) throw new NumberFormatException();
                } catch (Exception ex) {
                        throw new SignalMLException(_("Invalid data entered"));
                }
                
                descriptor.getOpenMonitorDescriptor().setSamplingFrequency(samplingFrequency);
                descriptor.getOpenMonitorDescriptor().setChannelCount(getChannelCountSpinner().getValue());
                descriptor.getOpenMonitorDescriptor().setPageSize(pageSize);
                descriptor.getOpenMonitorDescriptor().setByteOrder((RawSignalByteOrder) getByteOrderComboBox().getSelectedItem());
                descriptor.getOpenMonitorDescriptor().setSampleType((RawSignalSampleType) getSampleTypeComboBox().getSelectedItem());

                getEditGainAndOffsetDialog().fillModelFromDialog(descriptor);
        }

        /**
         * Fills {@link #currentModel} from this panel.
         *
         * @throws SignalMLException when input data is invalid
         */
        @Override
        protected void fillCurrentModelFromPanel() throws SignalMLException {

                fillModelFromPanel((AmplifierConnectionDescriptor) currentModel);
        }
}
