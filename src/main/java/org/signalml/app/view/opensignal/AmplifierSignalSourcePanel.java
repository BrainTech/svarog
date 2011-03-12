package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
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
         * Signal parameters panel.
         */
        private SignalParametersPanel signalParametersPanel = null;

        /**
         * Amplifier selection panel.
         */
        private AmplifierSelectionPanel amplifierSelectionPanel = null;

        /**
         * Monitor recording panel.
         */
        private MonitorRecordingPanel monitorRecordingPanel = null;

        /**
         * The start action.
         */
        private AbstractAction startAction = null;
        
        /**
         * The stop action.
         */
        private AbstractAction stopAction = null;

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
         * Gets the signal parameters panel.
         *
         * @return the signal parameters panel
         */
        private SignalParametersPanel getSignalParametersPanel() {

                if (signalParametersPanel == null) {
                        signalParametersPanel = new SignalParametersPanel(messageSource, viewerElementManager.getApplicationConfig());
                        signalParametersPanel.addPropertyChangeListener(this);                        
                }
                return signalParametersPanel;
        }

        /**
         * Gets the amplifier selection panel.
         *
         * @return the amplifier selection panel
         */
        private AmplifierSelectionPanel getAmplifierSelectionPanel() {

                if (amplifierSelectionPanel == null) {
                        amplifierSelectionPanel = new AmplifierSelectionPanel(
                                messageSource,
                                viewerElementManager,
                                getSignalParametersPanel(),
                                getMonitorRecordingPanel(),
                                getStartAction(),
                                getStopAction());
                }
                return amplifierSelectionPanel;
        }

        /**
         * Gets the monitor recording panel.
         *
         * @return the monitor recording panel
         */
        private MonitorRecordingPanel getMonitorRecordingPanel() {
                
                if (monitorRecordingPanel == null) {
                        monitorRecordingPanel = new MonitorRecordingPanel(messageSource);
                        monitorRecordingPanel.setEnabled(false);
                }
                return monitorRecordingPanel;
        }

        /**
         * Gets the start action.
         *
         * @return the start action
         */
        private AbstractAction getStartAction() {
                
                if (startAction == null) {
                        startAction = new StartAction();
                }
                return startAction;
        }

        /**
         * Gets the stop action.
         *
         * @return the stop action
         */
        private AbstractAction getStopAction() {

                if (stopAction == null) {
                        stopAction = new StopAction();
                }
                return stopAction;
        }

        /**
         * Fills this panel from a model.
         *
         * @param model the model
         * @throws SignalMLException when model is not supported or an amplifier
         * cannot be found (check {@link SignalMLException#getMessage()}
         */
        @Override
        public void fillPanelFromModel(Object model) throws SignalMLException {

                getSignalParametersPanel().fillPanelFromModel(model);
                getAmplifierSelectionPanel().fillPanelFromModel((AmplifierConnectionDescriptor) model);
                getMonitorRecordingPanel().fillModelFromPanel(((AmplifierConnectionDescriptor) model).getOpenMonitorDescriptor());
        }

        /**
         * Fills a model from this panel.
         *
         * @param model the model
         * @throws SignalMLException when model is not supported or input data
         * is not valid (check {@link SignalMLException#getMessage()}
         */
        @Override
        public void fillModelFromPanel(Object model) throws SignalMLException {

                getSignalParametersPanel().fillModelFromPanel(model);
                getAmplifierSelectionPanel().fillModelFromPanel((AmplifierConnectionDescriptor) model);
                getMonitorRecordingPanel().fillModelFromPanel(((AmplifierConnectionDescriptor) model).getOpenMonitorDescriptor());

                Montage channelTabMotntage = viewerElementManager.getOpenSignalAndSetMontageDialog().getChannelTabSourceMontage();
                String[] labels = new String[channelTabMotntage.getSourceChannelCount()];
                for (int i = 0; i < labels.length; i++) {
                        labels[i] = channelTabMotntage.getSourceChannelLabelAt(i);
                }
                AmplifierConnectionDescriptor descriptor = (AmplifierConnectionDescriptor) model;
                descriptor.getOpenMonitorDescriptor().setChannelLabels(labels);
        }

        /**
         * Responsible for starting the OpenBCI.
         *
         * @author Tomasz Sawicki
         */
        private class StartAction extends AbstractAction {

                /**
                 * When the action is performed.
                 *
                 * @param e action event
                 */
                @Override
                public void actionPerformed(ActionEvent e) {

                }

        }

        /**
         * Responsible for stoping the OpenBCI.
         *
         * @author Tomasz Sawicki
         */
        private class StopAction extends AbstractAction {

                /**
                 * Default constructor.
                 */
                public StopAction() {
                        
                        setEnabled(false);
                }

                /**
                 * When the action is performed.
                 *
                 * @param e action event
                 */
                @Override
                public void actionPerformed(ActionEvent e) {

                }
        }

}