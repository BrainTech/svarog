package org.signalml.app.view.opensignal;

import javax.swing.DefaultComboBoxModel;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.AmplifierConnectionDescriptor;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * A {@link SignalParametersPanel} for amplifier connection.
 *
 * @author Tomasz Sawicki
 */
public class SignalParametersPanelForAmplifierConnection extends AbstractSignalParametersPanel {

        /**
         * Default constructor.
         *
         * @param messageSource {@link #messageSource}
         * @param applicationConfiguration {@link #applicationConfiguration}
         */
        public SignalParametersPanelForAmplifierConnection(MessageSourceAccessor messageSource, ApplicationConfiguration applicationConfiguration) {

                super(messageSource, applicationConfiguration);
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
                        setEnabledAll(true);

                        getSamplingFrequencyComboBox().setEditable(false);
                        getSamplingFrequencyComboBox().setModel(new DefaultComboBoxModel(
                                descriptor.getAmplifierInstance().getDefinition().getAvailableFrequencies().toArray()));

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
                }

                currentModel = descriptor;
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
                } catch (NumberFormatException ex) {
                        throw new SignalMLException(messageSource.getMessage("error.invalidData"));
                }

                try {
                        pageSize = getPageSizeSpinner().getValue();
                        if (pageSize <= 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                        throw new SignalMLException(messageSource.getMessage("error.invalidData"));
                }

                descriptor.getOpenMonitorDescriptor().setSamplingFrequency(samplingFrequency);
                descriptor.getOpenMonitorDescriptor().setPageSize(pageSize);
                descriptor.getOpenMonitorDescriptor().setByteOrder((RawSignalByteOrder) getByteOrderComboBox().getSelectedItem());
                descriptor.getOpenMonitorDescriptor().setSampleType((RawSignalSampleType) getSampleTypeComboBox().getSelectedItem());
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
