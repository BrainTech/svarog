package org.signalml.app.view.monitor;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.app.view.dialog.AbstractPresetDialog;
import org.signalml.app.worker.amplifiers.AmplifierDefinition;
import org.signalml.plugin.export.SignalMLException;

/**
 * Amplifier definition configuration can be changed here.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierDefinitionConfigDialog extends AbstractPresetDialog {

        /**
         * Protocol label
         */
        private JLabel protocolLabel;
        /**
         * Protocol combo box.
         */
        private JComboBox protocolComboBox;
        /**
         * Match label.
         */
        private JLabel matchLabel;
        /**
         * Match text field.
         */
        private JTextField matchTextField;
        /**
         * Module name label.
         */
        private JLabel moduleNameLabel;
        /**
         * Module name text field.
         */
        private JComboBox moduleNameComboBox;
        /**
         * Amplifier null label.
         */
        private JLabel amplifierNullLabel;
        /**
         * Amplifier null text field.
         */
        private JTextField amplifierNullTextField;
        /**
         * Available frequencies panel.
         */
        private AvailableFrequenciesPanel availableFrequenciesPanel;
        /**
         * Channel definition panel.
         */
        private ChannelDefinitionPanel channelDefinitionPanel;
        /**
         * The viewer element manager.
         */
        private ViewerElementManager elementManager;

        /**
         * Default constructor.
         *
         * @param presetManager the preset manager
         * @param w parent window
         * @param isModal if this window is modal
         */
        public AmplifierDefinitionConfigDialog( PresetManager presetManager, Window w, boolean isModal, ViewerElementManager elementManager) {

                super( presetManager, w, isModal);
                this.elementManager = elementManager;
        }

        /**
         * Sets window's title then calls {@link AbstractDialog#initialize()}.
         */
        @Override
        protected void initialize() {

                setTitle(_("Amplifier definitions config"));
                super.initialize();
        }

        /**
         * Creates the interface.
         *
         * @return the interface
         */
        @Override
        protected JComponent createInterface() {
                
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.CENTER;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(3, 3, 3, 3);
                constraints.weighty = 0;

                CompoundBorder borderDriver = new CompoundBorder(
                        new TitledBorder(_("Driver data")),
                        new EmptyBorder(3, 3, 3, 3));

                JPanel textFieldsPanel = new JPanel(new GridBagLayout());
                textFieldsPanel.setBorder(borderDriver);

                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                textFieldsPanel.add(getProtocolLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 0;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                textFieldsPanel.add(getProtocolComboBox(), constraints);

                constraints.gridx = 0;
                constraints.gridy = 1;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                textFieldsPanel.add(getMatchLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 1;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                textFieldsPanel.add(getMatchTextField(), constraints);

                constraints.gridx = 0;
                constraints.gridy = 2;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                textFieldsPanel.add(getModuleNameLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 2;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                textFieldsPanel.add(getModuleNameComboBox(), constraints);


                CompoundBorder borderOther = new CompoundBorder(
                        new TitledBorder(_("Other data")),
                        new EmptyBorder(3, 3, 3, 3));

                JPanel otherPanel = new JPanel(new GridBagLayout());
                otherPanel.setBorder(borderOther);

                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                otherPanel.add(getAmplifierNullLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 0;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                otherPanel.add(getAmplifierNullTextField(), constraints);

                

                JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
                JPanel leftTopPanel = new JPanel(new BorderLayout(10, 10));
                leftTopPanel.add(textFieldsPanel, BorderLayout.NORTH);
                leftTopPanel.add(otherPanel, BorderLayout.SOUTH);
                leftPanel.add(leftTopPanel, BorderLayout.NORTH);
                leftPanel.add(getAvailableFrequenciesPanel(), BorderLayout.CENTER);
                
                
                JPanel mainPanel = new JPanel(new GridLayout(1, 2, 15, 15));
                mainPanel.add(leftPanel);
                mainPanel.add(getChannelDefinitionPanel());

                return mainPanel;
        }

        /**
         * Fills this dialog from an {@link AmplifierDefinition} object.
         *
         * @param model the model
         * @throws SignalMLException never thrown
         */
        @Override
        public void fillDialogFromModel(Object model) throws SignalMLException {

                AmplifierDefinition definition = (AmplifierDefinition) model;

                getProtocolComboBox().setSelectedItem(definition.getProtocol());
                getMatchTextField().setText(definition.getMatch());
                getModuleNameComboBox().setSelectedItem(definition.getModuleName());
                getAmplifierNullTextField().setText(definition.getAmplifierNull().toString());
                getAvailableFrequenciesPanel().setFrequencies(definition.getAvailableFrequencies());
                getChannelDefinitionPanel().setData(definition.getChannelNumbers(),
                        definition.getCalibrationGain(),
                        definition.getCalibrationOffset(),
                        definition.getDefaultNames());
        }

        /**
         * Fills an {@link AmplifierDefinition} object from this dialog.
         *
         * @param model the model
         * @throws SignalMLException never thrown
         */
        @Override
        public void fillModelFromDialog(Object model) throws SignalMLException {

                AmplifierDefinition definition = (AmplifierDefinition) model;

                definition.setProtocol((String) getProtocolComboBox().getSelectedItem());
                definition.setMatch(getMatchTextField().getText());
                definition.setModuleName(getModuleNameComboBox().getSelectedItem().toString());
                definition.setAvailableFrequencies(getAvailableFrequenciesPanel().getFrequencies());
                definition.setChannelNumbers(getChannelDefinitionPanel().getChannelNumbers());
                definition.setCalibrationGain(getChannelDefinitionPanel().getGainValues());
                definition.setCalibrationOffset(getChannelDefinitionPanel().getOffsetValues());
                definition.setDefaultNames(getChannelDefinitionPanel().getDefaultNames());

                try {
                        Double amplifierNull = Double.parseDouble(getAmplifierNullTextField().getText());
                        definition.setAmplifierNull(amplifierNull);
                } catch (NumberFormatException ex) {
                        throw new SignalMLException(_("Amplifier null: ")
                                + _("Please insert a rational value"));
                }
        }

        @Override
        public boolean supportsModelClass(Class<?> clazz) {

                return AmplifierDefinition.class.isAssignableFrom(clazz);
        }

        @Override
        public Preset getPreset() throws SignalMLException {

                AmplifierDefinition definition = new AmplifierDefinition();
                fillModelFromDialog(definition);
                return definition;
        }

        @Override
        public void setPreset(Preset preset) throws SignalMLException {

                fillDialogFromModel(preset);
        }

        @Override
        public boolean isCancellable() {

                return false;
        }

        /**
         * Gets the protocol label.
         *
         * @return the protocol label
         */
        private JLabel getProtocolLabel() {

                if (protocolLabel == null) {
                        protocolLabel = new JLabel();
                        protocolLabel.setText(_("Protocol: "));
                }
                return protocolLabel;
        }

        /**
         * Gets the protocol combo box.
         *
         * @return protocol combo box
         */
        private JComboBox getProtocolComboBox() {

                if (protocolComboBox == null) {
                        protocolComboBox = new JComboBox(new String[]{AmplifierDefinition.USB, AmplifierDefinition.BLUETOOTH});
                }
                return protocolComboBox;
        }

        /**
         * Gets the match label.
         *
         * @return the match label
         */
        private JLabel getMatchLabel() {

                if (matchLabel == null) {
                        matchLabel = new JLabel();
                        matchLabel.setText(_("Match: "));
                }
                return matchLabel;
        }

        /**
         * Gets the match text field.
         *
         * @return the match text field
         */
        private JTextField getMatchTextField() {

                if (matchTextField == null) {
                        matchTextField = new JTextField();
                }
                return matchTextField;
        }

        /**
         * Gets the module name label.
         *
         * @return the module name label
         */
        public JLabel getModuleNameLabel() {

                if (moduleNameLabel == null) {
                        moduleNameLabel = new JLabel();
                        moduleNameLabel.setText(_("Module name: "));
                }
                return moduleNameLabel;
        }

        /**
         * Gets the module name combo box.
         *
         * @return the module name combo box
         */
        public JComboBox getModuleNameComboBox() {

                if (moduleNameComboBox == null) {
                        moduleNameComboBox = new JComboBox();
                        moduleNameComboBox.setEditable(true);
                }
                return moduleNameComboBox;
        }

        /**
         * Gets the amplifier null label.
         * 
         * @return the amplifier null label
         */
        public JLabel getAmplifierNullLabel() {

                if (amplifierNullLabel == null) {
                        amplifierNullLabel = new JLabel();
                        amplifierNullLabel.setText(_("Amplifier null: "));
                }
                return amplifierNullLabel;
        }

        /**
         * Gets the amplifier null text field.
         *
         * @return the amplifier null text field
         */
        public JTextField getAmplifierNullTextField() {

                if (amplifierNullTextField == null) {
                        amplifierNullTextField = new JTextField();
                }
                return amplifierNullTextField;
        }

        /**
         * Gets the available frequencies panel.
         *
         * @return available frequencies panel
         */
        private AvailableFrequenciesPanel getAvailableFrequenciesPanel() {

                if (availableFrequenciesPanel == null) {
                        availableFrequenciesPanel = new AvailableFrequenciesPanel();
                }
                return availableFrequenciesPanel;
        }

        /**
         * Gets the channel definition panel.
         *
         * @return channel definition panel
         */
        private ChannelDefinitionPanel getChannelDefinitionPanel() {

                if (channelDefinitionPanel == null) {
                        channelDefinitionPanel = new ChannelDefinitionPanel();
                }
                return channelDefinitionPanel;
        }

        /**
         * Clear data on dialog close.
         */
        @Override
        protected void onDialogClose() {

                super.onDialogClose();
                getChannelDefinitionPanel().clearTextFields();
                getAvailableFrequenciesPanel().clearTextField();
        }

        /**
         * On dialog reset add module names to the combo box.
         */
        @Override
        protected void resetDialog() {

                super.resetDialog();
                getModuleNameComboBox().setModel(new DefaultComboBoxModel(
                        elementManager.getOpenBCIModulePresetManager().getAllModulesData().keySet().toArray()));

        }
}
