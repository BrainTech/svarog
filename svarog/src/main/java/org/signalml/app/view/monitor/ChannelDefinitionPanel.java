package org.signalml.app.view.monitor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Allows to insert channel definitions.
 *
 * @author Tomasz Sawicki
 */
public class ChannelDefinitionPanel extends JPanel implements ActionListener {

        /**
         * List of definitions.
         */
        private ChannelDefinitionsTable definitionsTable;

        /**
         * Channel no. label.
         */
        private JLabel channelLabel;

        /**
         * Channel no. textfield.
         */
        private JTextField channelTextField;

        /**
         * Gain label.
         */
        private JLabel gainLabel;

        /**
         * Gain textfield.
         */
        private JTextField gainTextField;

        /**
         * Offset label.
         */
        private JLabel offsetLabel;

        /**
         * Offset textfield.
         */
        private JTextField offsetTextField;

        /**
         * Offset label.
         */
        private JLabel defaultNameLabel;

        /**
         * Offset textfield.
         */
        private JTextField defaultNameTextField;

        /**
         * Insert button.
         */
        private JButton addButton;

        /**
         * Remove button.
         */
        private JButton removeButton;

        /**
         * Message source.
         */
        protected MessageSourceAccessor messageSource;

        /**
         * Default constructor.
         */
        public ChannelDefinitionPanel(MessageSourceAccessor messageSource) {

                super();
                this.messageSource = messageSource;
                createInterface();
        }

        /**
         * Creates the interface.
         */
        private void createInterface() {
                
                CompoundBorder border = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("amplifierDefinitionConfig.channelDefinitions")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

                setLayout(new BorderLayout(10, 10));

                add(new JScrollPane(getDefinitionsTable()), BorderLayout.CENTER);

                JPanel bottomPanel = new JPanel(new GridBagLayout());
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.CENTER;
                constraints.fill = GridBagConstraints.BOTH;
                constraints.insets = new Insets(3, 3, 3, 3);

                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                bottomPanel.add(getChannelLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 0;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                bottomPanel.add(getChannelTextField(), constraints);

                constraints.gridx = 0;
                constraints.gridy = 1;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                bottomPanel.add(getGainLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 1;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                bottomPanel.add(getGainTextField(), constraints);
                
                constraints.gridx = 0;
                constraints.gridy = 2;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                bottomPanel.add(getOffsetLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 2;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                bottomPanel.add(getOffsetTextField(), constraints);

                constraints.gridx = 0;
                constraints.gridy = 3;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                bottomPanel.add(getDefaultNameLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 3;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                bottomPanel.add(getDefaultNameTextField(), constraints);

                JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonsPanel.add(getAddButton());
                buttonsPanel.add(getRemoveButton());

                constraints.gridx = 2;
                constraints.gridy = 4;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                bottomPanel.add(buttonsPanel, constraints);

                add(bottomPanel, BorderLayout.PAGE_END);
        }

        /**
         * Called when buttons are clicked.
         *
         * @param e action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {

                if (getAddButton().equals(e.getSource())) {

                        ChannelDefinition definition = validateFields();
                        if (definition == null) return;
                        getDefinitionsTable().add(definition);
                        clearTextFields();
                }
                else if (getRemoveButton().equals(e.getSource())) {

                        getDefinitionsTable().removeSelected();
                }
        }

        /**
         * Validates fields and if they are OK: returns a ChannelDefinition object.
         *
         * @return ChannelDefition object, or null if fields are not ok
         */
        protected ChannelDefinition validateFields() {

                Integer channelno;
                try {
                        channelno = Integer.parseInt(getChannelTextField().getText());
                } catch (NumberFormatException ex) {
                        String errorMsg = messageSource.getMessage("amplifierDefinitionConfig.channelno") +
                        messageSource.getMessage("error.amplifierDefinitionConfig.integer");
                        JOptionPane.showMessageDialog(this, errorMsg);
                        return null;
                }

                Float gain;
                try {
                        gain = Float.parseFloat(getGainTextField().getText());
                } catch (NumberFormatException ex) {
                        String errorMsg = messageSource.getMessage("amplifierDefinitionConfig.gain") +
                        messageSource.getMessage("error.amplifierDefinitionConfig.rational");
                        JOptionPane.showMessageDialog(this, errorMsg);
                        return null;
                }

                Float offset;
                try {
                        offset = Float.parseFloat(getOffsetTextField().getText());
                } catch (NumberFormatException ex) {
                        String errorMsg = messageSource.getMessage("amplifierDefinitionConfig.offset") +
                        messageSource.getMessage("error.amplifierDefinitionConfig.rational");
                        JOptionPane.showMessageDialog(this, errorMsg);
                        return null;
                }

                return new ChannelDefinition(channelno, gain, offset, getDefaultNameTextField().getText());
        }

        /**
         * Gets the frequencies list
         *
         * @return the frequencies list
         */
        protected ChannelDefinitionsTable getDefinitionsTable() {

                if (definitionsTable == null) {
                        definitionsTable = new ChannelDefinitionsTable(messageSource, false);
                }
                return definitionsTable;
        }

        /**
         * Gets the add button.
         *
         * @return the add button
         */
        protected JButton getAddButton() {

                if (addButton == null) {
                        addButton = new JButton();
                        addButton.setText(messageSource.getMessage("amplifierDefinitionConfig.addButton"));
                        addButton.addActionListener(this);
                }
                return addButton;
        }

        /**
         * Gets the remove button.
         *
         * @return the remove button
         */
        protected JButton getRemoveButton() {

                if (removeButton == null) {
                        removeButton = new JButton();
                        removeButton.setText(messageSource.getMessage("amplifierDefinitionConfig.removeButton"));
                        removeButton.addActionListener(this);
                }
                return removeButton;
        }

        /**
         * Gets the channel label
         *
         * @return the channel label
         */
        protected JLabel getChannelLabel() {
                
                if (channelLabel == null) {
                        channelLabel = new JLabel();
                        channelLabel.setText(messageSource.getMessage("amplifierDefinitionConfig.channelno"));
                }
                return channelLabel;
        }

        /**
         * Gets the channel text field
         *
         * @return the channel text field
         */
        protected JTextField getChannelTextField() {

                if (channelTextField == null) {
                        channelTextField = new JTextField();
                }
                return channelTextField;
        }

        /**
         * Gets the gain label
         * 
         * @return the gain label
         */
        protected JLabel getGainLabel() {
                
                if (gainLabel == null) {
                        gainLabel = new JLabel();
                        gainLabel.setText(messageSource.getMessage("amplifierDefinitionConfig.gain"));
                }
                return gainLabel;
        }

        /**
         * Gets the gain text field
         *
         * @return the gain text field
         */
        protected JTextField getGainTextField() {

                if (gainTextField == null) {
                        gainTextField = new JTextField();
                }
                return gainTextField;
        }

        /**
         * Gets the offset label
         * 
         * @return the offset label
         */
        protected JLabel getOffsetLabel() {
                
                if (offsetLabel == null) {
                        offsetLabel = new JLabel();
                        offsetLabel.setText(messageSource.getMessage("amplifierDefinitionConfig.offset"));
                }
                return offsetLabel;
        }

        /**
         * Gets the offset text field
         *
         * @return the offset text field
         */
        protected JTextField getOffsetTextField() {

                if (offsetTextField == null) {
                        offsetTextField = new JTextField();
                }
                return offsetTextField;
        }

        /**
         * Gets the default name label.
         *
         * @return the default name label
         */
        public JLabel getDefaultNameLabel() {

                if (defaultNameLabel == null) {
                        defaultNameLabel = new JLabel();
                        defaultNameLabel.setText(messageSource.getMessage("amplifierDefinitionConfig.defaultName"));
                }
                return defaultNameLabel;
        }

        /**
         * Gets the default name text field.
         *
         * @return the default name text field
         */
        public JTextField getDefaultNameTextField() {

                if (defaultNameTextField == null) {
                        defaultNameTextField = new JTextField();
                }
                return defaultNameTextField;
        }

        /**
         * Gets the channel number
         *
         * @return list of channel numbers
         */
        public List<Integer> getChannelNumbers() {

                return getDefinitionsTable().getChannelNumbers();
        }

        /**
         * Gets the gain values
         *
         * @return list of gain values
         */
        public List<Float> getGainValues() {

                return getDefinitionsTable().getGainValues();
        }

        /**
         * Gets the offset values
         *
         * @return list of channel numbers
         */
        public List<Float> getOffsetValues() {

                return getDefinitionsTable().getOffsetValues();
        }

        /**
         * Gets the the default names.
         *
         * @return list of default names
         */
        public List<String> getDefaultNames() {

                return getDefinitionsTable().getDefaultNames();
        }

        /**
         * Sets the data
         *
         * @param numbers list od channel numbers
         * @param gainValues list of gain values
         * @param offsetValues list of offset values
         */
        public void setData(List<Integer> numbers, List<Float> gainValues, List<Float> offsetValues, List<String> names) {

                ArrayList<ChannelDefinition> definitions = new ArrayList<ChannelDefinition>();

                for (int i = 0; i < numbers.size(); i++) {

                        ChannelDefinition definition = new ChannelDefinition(numbers.get(i), gainValues.get(i), offsetValues.get(i), names.get(i));
                        definitions.add(definition);
                }

                getDefinitionsTable().setData(definitions);
        }

        /**
         * Clears text fields.
         */
        public void clearTextFields() {

                getChannelTextField().setText("");
                getGainTextField().setText("");
                getOffsetTextField().setText("");
        }
}