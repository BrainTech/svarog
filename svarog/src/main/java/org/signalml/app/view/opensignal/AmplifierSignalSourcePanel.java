package org.signalml.app.view.opensignal;

import org.signalml.app.model.AmplifierConnectionDescriptor;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.element.MonitorRecordingPanel;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;

/**
 * Panel enabling configuring, starting and connecting to OpenBCI.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierSignalSourcePanel extends AbstractMonitorSourcePanel {

        /**
         * Amplifier selection panel.
         */
        private AmplifierSelectionPanel amplifierSelectionPanel;
        /**
         * Panel to select which channels should be sent by
         * an OpenBCI system to Svarog.
         */
        private ChannelSelectPanel channelSelectPanel;
        /**
         * Panel containing buttons used to configure amps and modules.
         */
        private ConfigureAmplifiersPanel configureAmplifiersPanel;
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
         * Current descriptor.
         */
        private AmplifierConnectionDescriptor currentDescriptor;

        /**
         * Default constructor.
         *
         * @param viewerElementManager viewer element manager
         */
        public AmplifierSignalSourcePanel(ViewerElementManager viewerElementManager) {

                super(viewerElementManager);
        }

        /**
         * Creates left panel.
         *
         * @return the left panel
         */
        @Override
        protected JPanel createLeftColumnPanel() {

                JPanel leftColumnPanel = new JPanel(new BorderLayout());

                JPanel amplifierSelectionAndChannelSelectionPanel = new JPanel(new GridLayout(1, 2));
                amplifierSelectionAndChannelSelectionPanel.add(getAmplifierSelectionPanel());
                amplifierSelectionAndChannelSelectionPanel.add(getChannelSelectPanel());
                leftColumnPanel.add(amplifierSelectionAndChannelSelectionPanel, BorderLayout.CENTER);

                JPanel southPanels = new JPanel(new BorderLayout());
                southPanels.add(getStartStopButtonsPanel(), BorderLayout.NORTH);
                southPanels.add(getConfigureAmplifiersPanel(), BorderLayout.CENTER);
		southPanels.add(getTagPresetSelectionPanel(), BorderLayout.SOUTH);

                leftColumnPanel.add(southPanels, BorderLayout.SOUTH);
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

		JPanel lowerPanel = new JPanel(new BorderLayout());
		lowerPanel.add(getEegSystemSelectionPanel(), BorderLayout.NORTH);
                lowerPanel.add(getMonitorRecordingPanel(), BorderLayout.SOUTH);
		rightColumnPanel.add(lowerPanel, BorderLayout.SOUTH);
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
                                viewerElementManager,
                                this);
                }
                return amplifierSelectionPanel;
        }

        /**
         * Gets the channel select panel.
         *
         * @return the channel select panel.
         */
        public ChannelSelectPanel getChannelSelectPanel() {
                if (channelSelectPanel == null) {
                        channelSelectPanel = new ChannelSelectPanel();
                }
                return channelSelectPanel;
        }

        /**
         * Gets the configure amplifiers panel.
         *
         * @return the configure amplifiers panel
         */
        public ConfigureAmplifiersPanel getConfigureAmplifiersPanel() {
                if (configureAmplifiersPanel == null) {
                        configureAmplifiersPanel = new ConfigureAmplifiersPanel(viewerElementManager);
                }
                return configureAmplifiersPanel;
        }

        /**
         * Gets the start stop buttons panel.
         *
         * @return the start stop buttons panel
         */
        public StartStopButtonsPanel getStartStopButtonsPanel() {

                if (startStopButtonsPanel == null) {
                        startStopButtonsPanel = new StartStopButtonsPanel(this);
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
                        signalParametersPanel = new SignalParametersPanelForAmplifierConnection( viewerElementManager.getApplicationConfig());
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
                        monitorRecordingPanel = new MonitorRecordingPanel();
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
         * @param omitAmpList wheter to omit the amp list
         * @throws SignalMLException when amplifier cannot be found
         */
        public void fillPanelFromModel(AmplifierConnectionDescriptor descriptor, boolean omitAmpList) throws SignalMLException {

                setEnabledToPanels(!isMetadataFilled() && getAmplifierSignalDocument() == null);

                getSignalParametersPanel().fillPanelFromModel(descriptor);
                getChannelSelectPanel().fillPanelFromModel(descriptor);
                getAmplifierSelectionPanel().fillPanelFromModel(descriptor, omitAmpList);
		getTagPresetSelectionPanel().fillModelFromPanel(descriptor.getOpenMonitorDescriptor());
		getEegSystemSelectionPanel().setEegSystem(getEegSystemSelectionPanel().getSelectedEegSystem());
                
                currentDescriptor = descriptor;
        }

        /**
         * Fills a model from this panel.
         *
         * @param descriptor the descriptor
         * @throws SignalMLException when input data is not valid
         */
        public void fillModelFromPanel(AmplifierConnectionDescriptor descriptor) throws SignalMLException {

                getChannelSelectPanel().fillModelFromPanel(descriptor);
                getSignalParametersPanel().fillModelFromPanel(descriptor);
                getMonitorRecordingPanel().fillModelFromPanel(descriptor);
                getAmplifierSelectionPanel().fillModelFromPanel(descriptor);

                descriptor.getOpenMonitorDescriptor().setSignalSource(SignalSource.AMPLIFIER);
                descriptor.getOpenMonitorDescriptor().setMinimumValue(-1000f);
                descriptor.getOpenMonitorDescriptor().setMaximumValue(1000f);
		descriptor.getOpenMonitorDescriptor().setEegSystem(getEegSystemSelectionPanel().getSelectedEegSystem());
        }

        /**
         * Sets enabled to all subpanels.
         *
         * @param enabled enabled
         */
        private void setEnabledToPanels(boolean enabled) {

                getAmplifierSelectionPanel().setEnabledAll(enabled);
                getChannelSelectPanel().setEnabledAll(enabled);
                getSignalParametersPanel().setEnabledAll(enabled);
                getStartStopButtonsPanel().setEnabledAll(enabled);
        }

        /**
         * Fills {@link #currentDescriptor}, then returns it.
         *
         * @return filled {@link #currentDescriptor}
         * @throws SignalMLException when input data is invalid
         */
        public AmplifierConnectionDescriptor getFilledDescriptor() throws SignalMLException {

                fillModelFromPanel(currentDescriptor);
                return currentDescriptor;
        }

        /**
         * Gets {@link #currentDescriptor} without filling it.
         *
         * @return {@link #currentDescriptor}
         */
        public AmplifierConnectionDescriptor getDescriptor() {

                return currentDescriptor;
        }

        /**
         * Wheter metadata is filled.
         *
         * @return if metadata is filled.
         */
        @Override
        public boolean isMetadataFilled() {

                if (currentDescriptor == null) {
                        return false;
                }

                return currentDescriptor.isBciStarted();
        }

        /**
         * Gets the channel count from the {@link #signalParametersPanel}.
         *
         * @return the channel count
         */
        @Override
        public int getChannelCount() {

                return getSignalParametersPanel().getChannelCount();
        }

        /**
         * Gets the sampling frequency from the {@link #signalParametersPanel}.
         *
         * @return the sampling frequency
         */
        @Override
        public float getSamplingFrequency() {

                return getSignalParametersPanel().getSamplingFrequency();
        }

        /**
         * Gets the signal document created during amp connection.
         *
         * @return the document, or null if it doesn't exist
         */
        public MonitorSignalDocument getAmplifierSignalDocument() {

                DocumentManager manager = viewerElementManager.getDocumentManager();
                for (int i = 0; i < manager.getDocumentCount(); i++) {
                        Document document = manager.getDocumentAt(i);
                        if (document instanceof MonitorSignalDocument
                                && ((MonitorSignalDocument) document).getOpenMonitorDescriptor().getSignalSource().isAmplifier()) {
                                return ((MonitorSignalDocument) document);
                        }
                }
                return null;
        }

	@Override
	public void setSamplingFrequency(float samplingFrequency) {
		getSignalParametersPanel().getSamplingFrequencyComboBox().setSelectedItem(samplingFrequency);
	}
}
