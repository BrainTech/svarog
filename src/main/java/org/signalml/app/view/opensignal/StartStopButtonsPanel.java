package org.signalml.app.view.opensignal;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.model.AmplifierConnectionDescriptor;
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
         * The start button.
         */
        private JButton startButton;
        /**
         * The stop button.
         */
        private JButton stopButton;

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
                createInterface();
        }

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
                        startButton = new JButton();
                        startButton.setText(messageSource.getMessage("amplifierSelection.start"));
                }
                return startButton;
        }

        /**
         * Gets the stop button.
         *
         * @return the stop button
         */
        private JButton getStopButton() {

                if (stopButton == null) {
                        stopButton = new JButton();
                        stopButton.setText(messageSource.getMessage("amplifierSelection.stop"));
                }
                return stopButton;
        }
}