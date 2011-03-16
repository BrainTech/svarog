package org.signalml.app.view.opensignal;

import org.signalml.app.model.AmplifierConnectionDescriptor;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.element.MonitorRecordingPanel;
import org.signalml.domain.montage.Montage;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Panel enabling configuring, starting and connecting to OpenBCI.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierSignalSourcePanel extends AbstractSignalSourcePanel {

        /**
         * Amplifier selection panel.
         */
        private AmplifierSelectionPanel amplifierSelectionPanel;
        /**
         * Start stop buttons panel.
         */
        private StartStopButtonsPanel startStopButtonsPanel;
        /**
         * Signal parameters panel.
         */
        private SignalParametersPanelForAmplifierConnection signalParametersPanel;
        /**
         * Monitor recording panel.
         */
        private MonitorRecordingPanel monitorRecordingPanel;

        /**
         * Default constructor.
         *
         * @param messageSource message source
         * @param viewerElementManager viewer element manager
         */
        public AmplifierSignalSourcePanel(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {

                super(messageSource, viewerElementManager);

                try {
                        fillPanelFromModel(new AmplifierConnectionDescriptor());
                } catch (SignalMLException ex) {
                }
        }

        /**
         * Creates left panel.
         *
         * @return the left panel
         */
        @Override
        protected JPanel createLeftColumnPanel() {

                JPanel leftColumnPanel = new JPanel(new BorderLayout());
                leftColumnPanel.add(getAmplifierSelectionPanel(), BorderLayout.CENTER);
                leftColumnPanel.add(getStartStopButtonsPanel(), BorderLayout.PAGE_END);
                return leftColumnPanel;
        }

        /**
         * Creates right panel.
         *
         * @return the right panel
         */
        @Override
        protected JPanel createRightColumnPanel() {

                JPanel rightColumnPanel = new JPanel(new BorderLayout());
                rightColumnPanel.add(getSignalParametersPanel(), BorderLayout.CENTER);
                rightColumnPanel.add(getMonitorRecordingPanel(), BorderLayout.PAGE_END);
                return rightColumnPanel;
        }

        /**
         * Gets the amplifier selection panel.
         *
         * @return the amplifier selection panel
         */
        public AmplifierSelectionPanel getAmplifierSelectionPanel() {

                if (amplifierSelectionPanel == null) {
                        amplifierSelectionPanel = new AmplifierSelectionPanel(
                                messageSource,
                                viewerElementManager,
                                this);
                }
                return amplifierSelectionPanel;
        }

        /**
         * Gets the start stop buttons panel.
         *
         * @return the start stop buttons panel
         */
        public StartStopButtonsPanel getStartStopButtonsPanel() {

                if (startStopButtonsPanel == null) {
                        startStopButtonsPanel = new StartStopButtonsPanel(messageSource, this);
                }
                return startStopButtonsPanel;
        }

        /**
         * Gets the signal parameters panel.
         *
         * @return the signal parameters panel
         */
        public SignalParametersPanelForAmplifierConnection getSignalParametersPanel() {

                if (signalParametersPanel == null) {
                        signalParametersPanel = new SignalParametersPanelForAmplifierConnection(messageSource, viewerElementManager.getApplicationConfig());
                        signalParametersPanel.addPropertyChangeListener(this);
                }
                return signalParametersPanel;
        }

        /**
         * Gets the monitor recording panel.
         *
         * @return the monitor recording panel
         */
        public MonitorRecordingPanel getMonitorRecordingPanel() {

                if (monitorRecordingPanel == null) {
                        monitorRecordingPanel = new MonitorRecordingPanel(messageSource);
                        monitorRecordingPanel.setEnabled(false);
                }
                return monitorRecordingPanel;
        }

        /**
         * Fills this panel from a model.
         *
         * @param descriptor the descriptor
         * @throws SignalMLException when amplifier cannot be found
         */
        public void fillPanelFromModel(AmplifierConnectionDescriptor descriptor) throws SignalMLException {

                fillPanelFromModel(descriptor, false);
        }

        /**
         * Fills this panel from a model with an option to omit the {@link #amplifierSelectionPanel}.
         *
         * @param descriptor the descriptor
         * @param omitSelectionPanel wheter to omit the panel
         * @throws SignalMLException when amplifier cannot be found
         */
        public void fillPanelFromModel(AmplifierConnectionDescriptor descriptor, boolean omitSelectionPanel) throws SignalMLException {

                getSignalParametersPanel().fillPanelFromModel(descriptor);
                getMonitorRecordingPanel().fillPanelFromModel(descriptor);
                if (!omitSelectionPanel) getAmplifierSelectionPanel().fillPanelFromModel(descriptor);
        }

        /**
         * Fills a model from this panel.
         *
         * @param descriptor the descriptor
         * @throws SignalMLException when input data is not valid
         */
        public void fillModelFromPanel(AmplifierConnectionDescriptor descriptor) throws SignalMLException {

                getSignalParametersPanel().fillModelFromPanel(descriptor);
                getAmplifierSelectionPanel().fillModelFromPanel(descriptor);
                getMonitorRecordingPanel().fillModelFromPanel(descriptor);

                Montage channelTabMotntage = viewerElementManager.getOpenSignalAndSetMontageDialog().getChannelTabSourceMontage();
                String[] labels = new String[channelTabMotntage.getSourceChannelCount()];
                for (int i = 0; i < labels.length; i++) {
                        labels[i] = channelTabMotntage.getSourceChannelLabelAt(i);
                }
                descriptor.getOpenMonitorDescriptor().setChannelLabels(labels);
        }
}
