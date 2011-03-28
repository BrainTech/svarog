package org.signalml.app.view.opensignal;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.model.AmplifierConnectionDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.element.ProgressDialog;
import org.signalml.app.worker.OpenBCIManager;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Panel containing start and stop buttons.
 *
 * @author Tomasz Sawicki
 */
public class StartStopButtonsPanel extends JPanel {

        /**
         * The message source.
         */
        private MessageSourceAccessor messageSource;
        /**
         * Signal source panel.
         */
        private AmplifierSignalSourcePanel signalSourcePanel;
        /**
         * Vierwer element manager.
         */
        private ViewerElementManager elementManager;
        /**
         * The start button.
         */
        private JButton startButton;
        /**
         * The start action.
         */
        private StartAction startAction;
        /**false
         * The stop button.
         */
        private JButton stopButton;
        /**
         * The stop action.
         */
        private StopAction stopAction;
        /**
         * Current descriptor.
         */
        private AmplifierConnectionDescriptor currentDescriptor;

        /**
         * Default constructor.
         * 
         * @param messageSource {@link #messageSource}
         * @param signalSourcePanel {@link #signalSourcePanel}
         */
        public StartStopButtonsPanel(MessageSourceAccessor messageSource, AmplifierSignalSourcePanel signalSourcePanel) {

                super();
                this.messageSource = messageSource;
                this.signalSourcePanel = signalSourcePanel;
                this.elementManager = signalSourcePanel.getViewerElementManager();
                createInterface();
        }

        /**
         * Creates the interface.
         */
        private void createInterface() {

                CompoundBorder border = new CompoundBorder(
                        new TitledBorder(messageSource.getMessage("amplifierSelection.openBCI")),
                        new EmptyBorder(3, 3, 3, 3));

                setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
                setBorder(border);
                add(getStartButton());
                add(Box.createRigidArea(new Dimension(5, 0)));
                add(getStopButton());
        }

        /**
         * Gets the start button.
         *
         * @return the start button
         */
        private JButton getStartButton() {

                if (startButton == null) {
                        startButton = new JButton(getStartAction());
                        startButton.setText(messageSource.getMessage("amplifierSelection.start"));
                }
                return startButton;
        }

        /**
         * Gets the start action.
         *
         * @return the start action
         */
        private StartAction getStartAction() {

                if (startAction == null) {
                        startAction = new StartAction();
                }
                return startAction;
        }

        /**
         * Gets the stop button.
         *
         * @return the stop button
         */
        private JButton getStopButton() {

                if (stopButton == null) {
                        stopButton = new JButton(getStopAction());
                        stopButton.setText(messageSource.getMessage("amplifierSelection.stop"));
                }
                return stopButton;
        }

        /**
         * Gets the stop action.
         * 
         * @return the stop action
         */
        private StopAction getStopAction() {

                if (stopAction == null) {
                        stopAction = new StopAction();
                }
                return stopAction;
        }

        /**
         * Sets enabled to the buttons.
         *
         * @param enabled enabled
         */
        void setEnabledAll(boolean enabled) {

                getStartButton().setEnabled(enabled);
                getStopButton().setEnabled(!enabled);
        }

        /**
         * Action responsible for starting openbci.
         */
        private class StartAction extends AbstractAction {

                /**
                 * Tries to get a filled {@link AmplifierConnectionDescriptor},
                 * then starts a {@link OpenBCIManager} and shows {@link ProgressDialog}.
                 * After the work is done, if it was a success, the stop action is enabled,
                 * and all other panels are disabled.
                 *
                 * @param e action event
                 */
                @Override
                public void actionPerformed(ActionEvent e) {

                        // get filled descriptor, if it's impossible (e.g. input data invalid) show a message and exit
                        try {
                                currentDescriptor = signalSourcePanel.getFilledDescriptor();
                        } catch (SignalMLException ex) {
                                JOptionPane.showMessageDialog(null, ex.getMessage(), messageSource.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                                return;
                        }
                        if (currentDescriptor.getAmplifierInstance() == null) {
                                JOptionPane.showMessageDialog(null, messageSource.getMessage("opensignal.amplifier.selectAmplifier"),
                                        messageSource.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                                return;
                        }

                        // stop amp list refreshing.
                        signalSourcePanel.getAmplifierSelectionPanel().stopRefreshing();

                        // remove unused channels from the descriptor
                        removeUnusedChannels(currentDescriptor);

                        // create OpenBCIManager and progressDialog. attach dialog to the manager, start the
                        // manager and show the dialog.
                        OpenBCIManager manager = new OpenBCIManager(messageSource, elementManager, currentDescriptor);
                        ProgressDialog progressDialog = new ProgressDialog(messageSource,
                                signalSourcePanel.getViewerElementManager().getOpenSignalAndSetMontageDialog(),
                                true, messageSource.getMessage("opensignal.amplifier.startingBCI"));
                        manager.addPropertyChangeListener(progressDialog);
                        manager.execute();
                        progressDialog.showDialog();

                        // check wheter the execution was cancelled, or it ended with an error.
                        // exit if necessary.
                        if (progressDialog.wasCancelled()) {
                                manager.cancel();
                                return;
                        }
                        if (!progressDialog.wasSuccess()) {
                                return;
                        }

                        // if execution was a success, fill the descriptor ...
                        currentDescriptor.setBciStarted(true);
                        currentDescriptor.getOpenMonitorDescriptor().setJmxClient(elementManager.getJmxClient());
                        currentDescriptor.getOpenMonitorDescriptor().setTagClient(elementManager.getTagClient());
                        currentDescriptor.getOpenMonitorDescriptor().setMetadataReceived(true);

                        // ... and fill the main panel from it (so other panels are disabled).
                        try {
                                signalSourcePanel.fillPanelFromModel(currentDescriptor, true);
                        } catch (SignalMLException ex) {
                        }

                        // finaly set connected to true.
                        signalSourcePanel.setConnected(true);
                }

                /**
                 * Removes all unused channels from an {@link AmplifierConnectionDescriptor}.
                 * 
                 * @param currentDescriptor the descriptor
                 */
                private void removeUnusedChannels(AmplifierConnectionDescriptor descriptor) {

                        float[] descrGain = descriptor.getOpenMonitorDescriptor().getCalibrationGain();
                        float[] descrOffset = descriptor.getOpenMonitorDescriptor().getCalibrationOffset();
                        List<Integer> descrNumbers = descriptor.getAmplifierInstance().getDefinition().getChannelNumbers();                        

                        int[] selectedChannels = descriptor.getOpenMonitorDescriptor().getSelectedChannelsIndecies();
                        Integer selectedChannelsCount = new Integer(selectedChannels.length);
                        String[] selectedChannelsLabels = descriptor.getOpenMonitorDescriptor().getSelectedChannelsLabels();
                        
                        float[] selectedChannelsGain = new float[selectedChannels.length];
                        float[] selectedChannelsOffset = new float[selectedChannels.length];
                        List<Integer> selectedChannelNumbers = new ArrayList<Integer>();
                        

                        for (int i = 0; i < selectedChannelsCount; i++) {

                                selectedChannelsGain[i] = descrGain[selectedChannels[i]];
                                selectedChannelsOffset[i] = descrOffset[selectedChannels[i]];
                                selectedChannelNumbers.add(descrNumbers.get(selectedChannels[i]));
                        }

                        descriptor.getAmplifierInstance().getDefinition().setChannelNumbers(selectedChannelNumbers);
                        descriptor.getOpenMonitorDescriptor().setCalibrationGain(selectedChannelsGain);
                        descriptor.getOpenMonitorDescriptor().setCalibrationOffset(selectedChannelsOffset);
                        descriptor.getOpenMonitorDescriptor().setChannelCount(selectedChannelsCount);
                        descriptor.getOpenMonitorDescriptor().setChannelLabels(selectedChannelsLabels);
                        try {
                                descriptor.getOpenMonitorDescriptor().setSelectedChannelList(selectedChannelsLabels);
                        } catch (Exception ex) {
                        }
                }
        }

        /**
         * Action responsible for stopping openbci.
         */
        private class StopAction extends AbstractAction {
                
                /**
                 * Disconnects from multiplexer and kills all processes
                 * created during the connection. Stop the recording.
                 *
                 * @param e action event
                 */
                @Override
                public void actionPerformed(ActionEvent e) {

                        // launch the stop bci action
                        elementManager.getStopBCIAction().actionPerformed(e);

                        // get current descriptor, so that starting openbci right after
                        // stopping it is still possible.
                        AmplifierConnectionDescriptor descriptor = signalSourcePanel.getDescriptor();

                        // set bciStarted to false and fill the panel so all other panels
                        // are enabled. Also clear the amplifier list.
                        descriptor.setBciStarted(false);
                        try {
                                signalSourcePanel.fillPanelFromModel(descriptor, true);
                                signalSourcePanel.getAmplifierSelectionPanel().clearAmplifierList();
                        } catch (SignalMLException ex) {
                        }

                        // finally set connected to false.
                        signalSourcePanel.setConnected(false);
                }
        }
}
