package org.signalml.app.view.monitor;

import static org.signalml.app.SvarogApplication._;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.view.dialog.AbstractPresetDialog;
import org.signalml.app.view.element.FileSelectPanel;
import org.signalml.app.worker.processes.OpenBCIModule;
import org.signalml.plugin.export.SignalMLException;

/**
 * Allows to configure openbci modules' paths.
 *
 * @author Tomasz Sawicki
 */
public class OpenBCIModuleConfigDialog extends AbstractPresetDialog {

        /**
         * File select panel
         */
        private FileSelectPanel fileSelectPanel;
        /**
         * Delay label.
         */
        private JLabel delayLabel;
        /**
         * Delay text field.
         */
        private JTextField delayTextField;
        /**
         * Parameters label.
         */
        private JLabel parametersLabel;
        /**
         * Parameters text field.
         */
        private JTextField parametersTextField;

        /**
         * Default constructor.
         *
         * @param presetManager the preset manager
         * @param w parent window
         * @param isModal if this window is modal
         */
        public  OpenBCIModuleConfigDialog( PresetManager presetManger, Window w, boolean isModal) {

                super( presetManger, w, isModal);
        }

        /**
         * Sets window's title then calls {@link AbstractDialog#initialize()}.
         */
        @Override
        public void initialize() {

                setTitle(_("OpenBCI modules config"));
                super.initialize();
        }

        /**
         * Creates the interface.
         *
         * @return the interface
         */
        @Override
        protected JComponent createInterface() {

                JPanel mainPanel = new JPanel(new GridBagLayout());
                CompoundBorder panelBorder = new CompoundBorder(new TitledBorder(""), new EmptyBorder(3, 3, 3, 3));
                mainPanel.setBorder(panelBorder);

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.CENTER;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(3, 3, 3, 3);
                constraints.weighty = 0;

                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.gridwidth = 3;
                constraints.weightx = 1;
                mainPanel.add(getFileSelectPanel(), constraints);

                constraints.gridx = 0;
                constraints.gridy = 1;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                mainPanel.add(getParametersLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 1;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                mainPanel.add(getParametersTextField(), constraints);

                constraints.gridx = 0;
                constraints.gridy = 2;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                mainPanel.add(getDelayLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 2;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                mainPanel.add(getDelayTextField(), constraints);

                return mainPanel;
        }

        @Override
        public void fillDialogFromModel(Object model) throws SignalMLException {

                OpenBCIModule module = (OpenBCIModule) model;

                getFileSelectPanel().setFileName(module.getPath());
                getDelayTextField().setText(module.getDelay().toString());
                getParametersTextField().setText(module.getParameters());
        }

        @Override
        public void fillModelFromDialog(Object model) throws SignalMLException {

                OpenBCIModule module = (OpenBCIModule) model;

                module.setPath(getFileSelectPanel().getFileName());
                module.setParameters(getParametersTextField().getText());

                try {
                        Integer delay = Integer.parseInt(getDelayTextField().getText());
                        module.setDelay(delay);
                } catch (NumberFormatException ex) {
                        throw new SignalMLException(_("Delay [ms]: ")
                                + _("Please insert an integer value"));
                }
        }

        @Override
        public Preset getPreset() throws SignalMLException {

                OpenBCIModule module = new OpenBCIModule();
                fillModelFromDialog(module);
                return module;
        }

        @Override
        public void setPreset(Preset preset) throws SignalMLException {

                fillDialogFromModel(preset);
        }

        @Override
        public boolean supportsModelClass(Class<?> clazz) {

                return OpenBCIModule.class.isAssignableFrom(clazz);
        }

        @Override
        public boolean isCancellable() {

                return false;
        }

        /**
         * Gets the file select panel.
         *
         * @return the file select panel
         */
        private FileSelectPanel getFileSelectPanel() {

                if (fileSelectPanel == null) {
                        fileSelectPanel = new FileSelectPanel( _("Module path: "));
                        fileSelectPanel.returnRelativePath(true);
                }
                return fileSelectPanel;
        }

        /**
         * Gets the delay label.
         *
         * @return the the delay label
         */
        public JLabel getDelayLabel() {

                if (delayLabel == null) {
                        delayLabel = new JLabel();
                        delayLabel.setText(_("Delay [ms]: "));
                }
                return delayLabel;
        }

        /**
         * Gets the delay text field.
         *
         * @return the delay text field
         */
        public JTextField getDelayTextField() {

                if (delayTextField == null) {
                        delayTextField = new JTextField();
                }
                return delayTextField;
        }

        /**
         * Gets the parameters label.
         *
         * @return the parameters label
         */
        public JLabel getParametersLabel() {

                if (parametersLabel == null) {
                        parametersLabel = new JLabel();
                        parametersLabel.setText(_("Parameters: "));
                }
                return parametersLabel;
        }

        /**
         * Gets the parameters text field.
         *
         * @return the parameters text field
         */
        public JTextField getParametersTextField() {

                if (parametersTextField == null) {
                        parametersTextField = new JTextField();
                }
                return parametersTextField;
        }
}