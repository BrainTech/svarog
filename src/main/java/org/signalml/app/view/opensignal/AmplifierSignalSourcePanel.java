package org.signalml.app.view.opensignal;

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
        private AmplifierSelectionPanel amplifierSelectionPanel = null;
        /**
         * Start stop buttons panel.
         */
        private StartStopButtonsPanel startStopButtonsPanel = null;
        /**
         * Signal parameters panel.
         */
        private SignalParametersPanel signalParametersPanel = null;
        /**
         * Monitor recording panel.
         */
        private MonitorRecordingPanel monitorRecordingPanel = null;
        /**
         * Current descriptor.
         */
        private AmplifierConnectionDescriptor currentDescriptor = null;

        /**
         * Default constructor.
         *
         * @param messageSource message source
         * @param viewerElementManager viewer element manager
         */
        public AmplifierSignalSourcePanel(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {

                super(messageSource, viewerElementManager);
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
                                getSignalParametersPanel(),
                                getMonitorRecordingPanel());
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
        public SignalParametersPanel getSignalParametersPanel() {

                if (signalParametersPanel == null) {
                        signalParametersPanel = new SignalParametersPanel(messageSource, viewerElementManager.getApplicationConfig());
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

        /**at org.signalml.app.view.opensignal.AmplifierSelectionPanel.fillModelFromPanel(AmplifierSelectionPanel.java:114)
         * Fills this panel from a model.
         *
         * @param model the model
         * @throws SignalMLException when model is not supported or an amplifier
         * cannot be found (check {@link SignalMLException#getMessage()}
         */
        public void fillPanelFromModel(Object model) throws SignalMLException {

                getSignalParametersPanel().fillPanelFromModel(model);
                getAmplifierSelectionPanel().fillPanelFromModel((AmplifierConnectionDescriptor) model);

                currentDescriptor = (AmplifierConnectionDescriptor) model;
        }

        /**
         * Fills a model from this panel.
         *
         * @param model the model
         * @throws SignalMLException when model is not supported or input data
         * is not valid (check {@link SignalMLException#getMessage()}
         */
        public void fillModelFromPanel(Object model) throws SignalMLException {

                getSignalParametersPanel().fillModelFromPanel(model);
                getAmplifierSelectionPanel().fillModelFromPanel((AmplifierConnectionDescriptor) model);
                getMonitorRecordingPanel().fillModelFromPanel(((AmplifierConnectionDescriptor) model));

                Montage channelTabMotntage = viewerElementManager.getOpenSignalAndSetMontageDialog().getChannelTabSourceMontage();
                String[] labels = new String[channelTabMotntage.getSourceChannelCount()];
                for (int i = 0; i < labels.length; i++) {
                        labels[i] = channelTabMotntage.getSourceChannelLabelAt(i);
                }
                AmplifierConnectionDescriptor descriptor = (AmplifierConnectionDescriptor) model;
                descriptor.getOpenMonitorDescriptor().setChannelLabels(labels);
        }
}
