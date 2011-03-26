package org.signalml.app.view.opensignal;

import org.signalml.app.model.AmplifierConnectionDescriptor;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.element.MonitorRecordingPanel;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

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

		JPanel amplifierSelectionAndChannelSelectionPanel = new JPanel(new GridLayout(1, 2));
		amplifierSelectionAndChannelSelectionPanel.add(getAmplifierSelectionPanel());
		amplifierSelectionAndChannelSelectionPanel.add(getChannelSelectPanel());
                leftColumnPanel.add(amplifierSelectionAndChannelSelectionPanel, BorderLayout.CENTER);

		JPanel southPanels = new JPanel(new BorderLayout());
		southPanels.add(getStartStopButtonsPanel(), BorderLayout.NORTH);
                southPanels.add(getConfigureAmplifiersPanel(), BorderLayout.SOUTH);
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
         * Gets the channel select panel.
         *
         * @return the channel select panel.
         */
	public ChannelSelectPanel getChannelSelectPanel() {
		if (channelSelectPanel == null) {
			channelSelectPanel = new ChannelSelectPanel(messageSource);
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
			configureAmplifiersPanel = new ConfigureAmplifiersPanel(messageSource, viewerElementManager);
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

                descriptor.setBciStarted(getStartStopButtonsPanel().isBCIStarted());

		getChannelSelectPanel().fillPanelFromModel(descriptor);
                getSignalParametersPanel().fillPanelFromModel(descriptor);                
                if (!omitSelectionPanel) {
                        getAmplifierSelectionPanel().fillPanelFromModel(descriptor);
                }

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

                descriptor.getOpenMonitorDescriptor().setMinimumValue(-1000f);
                descriptor.getOpenMonitorDescriptor().setMaximumValue(1000f);
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
         * Wheter metadata is filled.
         *
         * @return if metadata is filled.
         */
        @Override
        public boolean isMetadataFilled() {
                return getStartStopButtonsPanel().isBCIStarted();
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
}
