package org.signalml.app.view.opensignal;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import multiplexer.jmx.client.JmxClient;
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
         * BCI started.
         */
        private boolean bciStarted;
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

	public boolean isBCIStarted() {
		return bciStarted;
	}

        /**
         * Fills an {@link AmplifierConnectionDescriptor} from this panel.
         *
         * @param descriptor the descriptor
         */
        public void fillModelFromPanel(AmplifierConnectionDescriptor descriptor) {

                descriptor.setBciStarted(bciStarted);
                if (bciStarted) {
                        descriptor.getOpenMonitorDescriptor().setJmxClient(elementManager.getJmxClient());
                        descriptor.getOpenMonitorDescriptor().setTagClient(elementManager.getTagClient());
                        descriptor.getOpenMonitorDescriptor().setMetadataReceived(true);
                } else {
                        descriptor.getOpenMonitorDescriptor().setJmxClient(null);
                        descriptor.getOpenMonitorDescriptor().setTagClient(null);
                        descriptor.getOpenMonitorDescriptor().setMetadataReceived(false);
                }
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

                        OpenBCIManager manager = new OpenBCIManager(messageSource, elementManager, currentDescriptor);
                        ProgressDialog progressDialog = new ProgressDialog(messageSource,
                                signalSourcePanel.getViewerElementManager().getOpenSignalAndSetMontageDialog(),
                                true, messageSource.getMessage("opensignal.amplifier.startingBCI"));
                        manager.addPropertyChangeListener(progressDialog);
                        manager.execute();
                        progressDialog.showDialog();

                        if (progressDialog.wasCancelled()) {
                                manager.cancel();
                                return;
                        }

                        if (!progressDialog.wasSuccess()) {
                                return;
                        }

                        bciStarted = true;
			signalSourcePanel.setConnected(true);

                        fillModelFromPanel(currentDescriptor);
                        try {
                                signalSourcePanel.fillPanelFromModel(currentDescriptor);
                        } catch (SignalMLException ex) {
                        }

                        getStopAction().setEnabled(true);
                        setEnabled(false);
                }
        }

        /**
         * Action responsible for stopping openbci.
         */
        private class StopAction extends AbstractAction {

                /**
                 * Default constructor.
                 */
                public StopAction() {
                        super();
                        setEnabled(false);
                }

                /**
                 * Disconnects from multiplexer and kills all processes
                 * created during the connection. Enables start action
                 * and all other panels.
                 *
                 * @param e action event
                 */
                @Override
                public void actionPerformed(ActionEvent e) {

                        try {
                                JmxClient jmxClient = elementManager.getJmxClient();
                                if (jmxClient != null) {
                                        jmxClient.shutdown();
                                }
                                elementManager.setJmxClient(null);
                        } catch (InterruptedException ex) {
                        }

                        elementManager.getProcessManager().killAll();

                        getStartAction().setEnabled(true);
                        setEnabled(false);

                        bciStarted = false;
			signalSourcePanel.setConnected(false);
                        
                        fillModelFromPanel(currentDescriptor);
                        try {
                                signalSourcePanel.fillPanelFromModel(currentDescriptor);
                        } catch (SignalMLException ex) {
                        }

                        // TODO close the document?
                }
        }
}
