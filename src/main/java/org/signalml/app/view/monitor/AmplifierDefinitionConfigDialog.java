package org.signalml.app.view.monitor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
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
import org.signalml.app.view.dialog.AbstractPresetDialog;
import org.signalml.app.worker.amplifiers.AmplifierDefinition;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Amplifier definition configuration can be changed here.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierDefinitionConfigDialog extends AbstractPresetDialog {

        /**
         * Protocol label
         */
        private JLabel protocolLabel = null;

        /**
         * Protocol combo box.
         */
        private JComboBox protocolComboBox = null;

        /**
         * Match label.
         */
        private JLabel matchLabel = null;

        /**
         * Match text field.
         */
        private JTextField matchTextField = null;

        /**
         * Path label.
         */
        private JLabel pathLabel = null;

        /**
         * Path text field.
         */
        private JTextField pathTextField = null;

        /**
         * Available frequencies panel.
         */
        private AvailableFrequenciesPanel availableFrequenciesPanel = null;
        
        /**
         * Channel definition panel.
         */
        private ChannelDefinitionPanel channelDefinitionPanel = null;

        /**
         * Default constructor.
         *
         * @param messageSource the message source
         * @param presetManager the preset manager
         * @param w parent window
         * @param isModal if this window is modal
         */
        public AmplifierDefinitionConfigDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w, boolean isModal) {

                super(messageSource, presetManager, w, isModal);
        }

        /**
         * Sets window's title then calls {@link AbstractDialog#initialize()}.
         */
	@Override
	protected void initialize() {

                setTitle(messageSource.getMessage("amplifierDefinitionConfig.title"));
                super.initialize();
        }

        /**
         * Creates the interface.
         *
         * @return the interface
         */
        @Override
        protected JComponent createInterface() {

                JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

                CompoundBorder border = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("amplifierDefinitionConfig.driverData")),
			new EmptyBorder(3, 3, 3, 3));
                                
                JPanel textFieldsPanel = new JPanel(new GridBagLayout());
                textFieldsPanel.setBorder(border);

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.CENTER;
                constraints.fill = GridBagConstraints.HORIZONTAL;

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
                textFieldsPanel.add(getPathLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 2;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                textFieldsPanel.add(getPathTextField(), constraints);

                mainPanel.add(textFieldsPanel, BorderLayout.PAGE_START);

                JPanel listsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
                listsPanel.add(getAvailableFrequenciesPanel());
                listsPanel.add(getChannelDefinitionPanel());

                mainPanel.add(listsPanel, BorderLayout.CENTER);

                return mainPanel;
        }

        @Override
        public void fillDialogFromModel(Object model) throws SignalMLException {

                AmplifierDefinition definition = (AmplifierDefinition) model;

                getProtocolComboBox().setSelectedItem(definition.getProtocol());
                getMatchTextField().setText(definition.getMatch());
                getPathTextField().setText(definition.getDriverPath());
                getAvailableFrequenciesPanel().setFrequencies(definition.getAvailableFrequencies());
                getChannelDefinitionPanel().setData(definition.getChannelNumbers(),
                                                    definition.getChannelGain(),
                                                    definition.getChannelOffset());
        }

        @Override
        public void fillModelFromDialog(Object model) throws SignalMLException {

                AmplifierDefinition definition = (AmplifierDefinition) model;

                definition.setProtocol((String) getProtocolComboBox().getSelectedItem());
                definition.setMatch(getMatchTextField().getText());
                definition.setDriverPath(getPathTextField().getText());
                definition.setAvailableFrequencies(getAvailableFrequenciesPanel().getFrequencies());
                definition.setChannelNumbers(getChannelDefinitionPanel().getChannelNumbers());
                definition.setChannelGain(getChannelDefinitionPanel().getGainValues());
                definition.setChannelOffset(getChannelDefinitionPanel().getOffsetValues());
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
                        protocolLabel.setText(messageSource.getMessage("amplifierDefinitionConfig.protocol"));
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
                        protocolComboBox = new JComboBox(new String[] { AmplifierDefinition.USB, AmplifierDefinition.BLUETOOTH });
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
                        matchLabel.setText(messageSource.getMessage("amplifierDefinitionConfig.match"));
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
         * Gets the path label.
         *
         * @return the path label
         */
        private JLabel getPathLabel() {

                if (pathLabel == null) {
                        pathLabel = new JLabel();
                        pathLabel.setText(messageSource.getMessage("amplifierDefinitionConfig.driverPath"));
                }
                return pathLabel;
        }

        /**
         * Gets the path text field.
         *
         * @return the path text field
         */
        private JTextField getPathTextField() {

                if (pathTextField == null) {
                        pathTextField = new JTextField();
                }
                return pathTextField;
        }

        /**
         * Gets the available frequencies panel.
         *
         * @return available frequencies panel
         */
        private AvailableFrequenciesPanel getAvailableFrequenciesPanel() {

                if (availableFrequenciesPanel == null) {
                        availableFrequenciesPanel = new AvailableFrequenciesPanel(messageSource);
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
                        channelDefinitionPanel = new ChannelDefinitionPanel(messageSource);
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
}