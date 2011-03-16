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
import org.signalml.app.model.OpenMonitorDescriptor;
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
        /**
         * The stop button.
         */
        private JButton stopButton;
        /**
         * The stop action.
         */
        private StopAction stopAction;

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
         * Action responsible for starting openbci.
         */
        private class StartAction extends AbstractAction {

                /**
                 * Tries to get a filled {@link AmplifierConnectionDescriptor},
                 * then starts a {@link OpenBCIManager} and shows {@link ProgressDialog}.
                 * After the work is done, if it was a success, the stop action is enabled,
                 * and a new document is opened.
                 *
                 * @param e action event
                 */
                @Override
                public void actionPerformed(ActionEvent e) {

                        AmplifierConnectionDescriptor descriptor = new AmplifierConnectionDescriptor();
                        try {
                                signalSourcePanel.fillModelFromPanel(descriptor);
                        } catch (SignalMLException ex) {
                                JOptionPane.showMessageDialog(null, ex.getMessage(), messageSource.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                                return;
                        }

                        if (descriptor.getAmplifierInstance() == null) {
                                JOptionPane.showMessageDialog(null, messageSource.getMessage("opensignal.amplifier.selectAmplifier"),
                                        messageSource.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                                return;
                        }

                        OpenMonitorDescriptor monitorDescriptor = descriptor.getOpenMonitorDescriptor();
                        monitorDescriptor.setMultiplexerAddress(elementManager.getApplicationConfig().getMultiplexerAddress());
                        monitorDescriptor.setMultiplexerPort(elementManager.getApplicationConfig().getMultiplexerPort());

                        OpenBCIManager manager = new OpenBCIManager(messageSource, elementManager, descriptor);
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

                        monitorDescriptor.setJmxClient(elementManager.getJmxClient());
                        monitorDescriptor.setTagClient(elementManager.getTagClient());

                        getStopAction().setEnabled(true);
                        setEnabled(false);

                        // TODO open the document?
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
                 * created during the connection.
                 *
                 * @param e action event
                 */
                @Override
                public void actionPerformed(ActionEvent e) {

                        try {
                                JmxClient jmxClient = elementManager.getJmxClient();
                                if (jmxClient != null)
                                        jmxClient.shutdown();
                                elementManager.setJmxClient( null);
                        }
                        catch (InterruptedException ex) {
                        }

                        elementManager.getProcessManager().killAll();

                        getStartAction().setEnabled(true);
                        setEnabled(false);

                        // TODO close the document?
                }
        }
}
