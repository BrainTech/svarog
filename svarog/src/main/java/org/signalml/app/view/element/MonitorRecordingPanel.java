/* MonitorRecordingPanel.java created 2010-11-09
 *
 */
package org.signalml.app.view.element;

import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.monitor.ChooseFilesForMonitorRecordingPanel;
import org.signalml.app.view.dialog.OpenMonitorDialog;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.model.AmplifierConnectionDescriptor;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.validation.BindException;

/**
 * Represents a panel in the {@link OpenMonitorDialog} used to enable/disable
 * monitor recording and set files to which the monitor should be recorded
 * (if recording is enabled).
 *
 * @author Piotr Szachewicz
 */
public class MonitorRecordingPanel extends AbstractSignalMLPanel {

        /**
         * A panel for choosing signal and tags recording target files for this
         * recording.
         */
        private ChooseFilesForMonitorRecordingPanel chooseFilesForMonitorRecordingPanel = null;
        /**
         * A panel containing the {@link MonitorRecordingPanel#enableRecordingCheckbox}.
         */
        private JPanel enableRecordingPanel = null;
        /**
         * A {@link JCheckBox} for enabling/disabling signal and tags recording.
         */
        private JCheckBox enableRecordingCheckbox = null;

        /**
         * Constructor. Creates a new {@link MonitorRecordingPanel}.
         * @param messageSource the message source accessor capable of resolving
         * localized message codes
         */
        public MonitorRecordingPanel(MessageSourceAccessor messageSource) {
                super(messageSource);
                initialize();
        }

        /**
         * Initializes all components.
         */
        private void initialize() {

                setLayout(new BorderLayout(10, 10));

                CompoundBorder border = new CompoundBorder(
                        new TitledBorder(messageSource.getMessage("openMonitor.monitorRecordingPanelTitle")),
                        new EmptyBorder(3, 3, 3, 3));
                setBorder(border);

                add(getEnableRecordingPanel(), BorderLayout.NORTH);
                add(getChooseFilesForMonitorRecordingPanel(), BorderLayout.CENTER);

                getEnableRecordingCheckbox().setSelected(false);
                getChooseFilesForMonitorRecordingPanel().setEnabled(false);

        }

        /**
         * Returns the panel for choosing signal and tags recording target files
         * used in this {@link MonitorRecordingPanel}
         * @return the {@link ChooseFilesForMonitorRecordingPanel} used
         */
        protected ChooseFilesForMonitorRecordingPanel getChooseFilesForMonitorRecordingPanel() {
                if (chooseFilesForMonitorRecordingPanel == null) {
                        chooseFilesForMonitorRecordingPanel = new ChooseFilesForMonitorRecordingPanel(messageSource);
                }
                return chooseFilesForMonitorRecordingPanel;
        }

        /**
         * Returns the panel for enabling/disabling signal and tags recording
         * used in this {@link MonitorRecordingPanel}
         * @return the panel for enabling/disabling monitor recording
         */
        protected JPanel getEnableRecordingPanel() {
                if (enableRecordingPanel == null) {
                        enableRecordingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        enableRecordingPanel.add(getEnableRecordingCheckbox());
                        enableRecordingPanel.add(new JLabel("enable recording"));
                }
                return enableRecordingPanel;
        }

        /**
         * Returns the {@link JCheckBox} for enabling/disabling monitor recording
         * used in this panel.
         * @return a {@link JCheckBox} for enabling/disabling monitor recording
         */
        protected JCheckBox getEnableRecordingCheckbox() {
                if (enableRecordingCheckbox == null) {
                        enableRecordingCheckbox = new JCheckBox();
                        enableRecordingCheckbox.addItemListener(new ItemListener() {

                                @Override
                                public void itemStateChanged(ItemEvent e) {
                                        if (enableRecordingCheckbox.isSelected()) {
                                                getChooseFilesForMonitorRecordingPanel().setEnabled(true);
                                        } else {
                                                getChooseFilesForMonitorRecordingPanel().setEnabled(false);
                                        }
                                }
                        });
                }
                return enableRecordingCheckbox;
        }

        /**
         * Returns whether recording was enabled on this panel.
         * @return true if recording was enabled, false otherwise.
         */
        public boolean isRecordingEnabled() {
                return enableRecordingCheckbox.isSelected();
        }

        /**
         * Fills the model with the data from this panel (user input).
         * @param openMonitorDescriptor the model to be filled.
         */
        public void fillModelFromPanel(OpenMonitorDescriptor openMonitorDescriptor) {
                if (isRecordingEnabled()) {
                        openMonitorDescriptor.getMonitorRecordingDescriptor().setRecordingEnabled(true);
                        getChooseFilesForMonitorRecordingPanel().fillModelFromPanel(openMonitorDescriptor);
                } else {
                        openMonitorDescriptor.getMonitorRecordingDescriptor().setRecordingEnabled(false);
                }
        }

        /**
         * Fills a {@link AmplifierConnectionDescriptor}.
         * @param amplifierConnectionDescriptor the model to be filled.
         * @throws SignalMLException when input data is invalid
         */
        public void fillModelFromPanel(AmplifierConnectionDescriptor descriptor) throws SignalMLException {

                Errors errors = new BindException(this, "data");
                validatePanel(this, errors);
                if (errors.hasErrors())
                        throw new SignalMLException(messageSource.getMessage("error.invalidData"));


                OpenMonitorDescriptor openMonitorDescriptor = descriptor.getOpenMonitorDescriptor();
                if (isRecordingEnabled()) {
                        openMonitorDescriptor.getMonitorRecordingDescriptor().setRecordingEnabled(true);
                        getChooseFilesForMonitorRecordingPanel().fillModelFromPanel(openMonitorDescriptor);
                } else {
                        openMonitorDescriptor.getMonitorRecordingDescriptor().setRecordingEnabled(false);
                }
        }

        /**
         * Checks if this dialog is properly filled.
         * @param model the model for this dialog
         * @param errors the object in which errors are stored
         */
        public void validatePanel(Object model, Errors errors) {
                if (isRecordingEnabled()) {
                        getChooseFilesForMonitorRecordingPanel().validatePanel(model, errors);
                }
        }

        /**
         * Resets the signal and tag filenames entered in the panel to an empty
         * string.
         */
        public void resetFileNames() {
                getChooseFilesForMonitorRecordingPanel().resetFileNames();
        }

}