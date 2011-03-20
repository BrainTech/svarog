package org.signalml.app.view.monitor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
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
        private JList definitionsList;

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

                add(new JScrollPane(getDefinitionsList()), BorderLayout.CENTER);

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

                JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonsPanel.add(getAddButton());
                buttonsPanel.add(getRemoveButton());

                constraints.gridx = 2;
                constraints.gridy = 3;
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

                if (addButton.equals(e.getSource())) {

                        ChannelDefinition definition = validateFields();
                        if (definition == null) return;

                        List<ChannelDefinition> definitions = getChannelDefinitions();
                        definitions.add(definition);
                        definitionsList.setListData(definitions.toArray());

                        clearTextFields();
                }
                else if (removeButton.equals(e.getSource())) {

                        int selectedIndex = definitionsList.getSelectedIndex();

                        if (selectedIndex == -1 )
                                return;

                        List<ChannelDefinition> definitions = getChannelDefinitions();
                        definitions.remove(selectedIndex);
                        definitionsList.setListData(definitions.toArray());

                        definitionsList.setSelectedIndex(-1);
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
                        channelno = Integer.parseInt(channelTextField.getText());
                } catch (NumberFormatException ex) {
                        String errorMsg = messageSource.getMessage("amplifierDefinitionConfig.channelno") +
                        messageSource.getMessage("error.amplifierDefinitionConfig.integer");
                        JOptionPane.showMessageDialog(this, errorMsg);
                        return null;
                }

                Float gain;
                try {
                        gain = Float.parseFloat(gainTextField.getText());
                } catch (NumberFormatException ex) {
                        String errorMsg = messageSource.getMessage("amplifierDefinitionConfig.gain") +
                        messageSource.getMessage("error.amplifierDefinitionConfig.rational");
                        JOptionPane.showMessageDialog(this, errorMsg);
                        return null;
                }

                Float offset;
                try {
                        offset = Float.parseFloat(offsetTextField.getText());
                } catch (NumberFormatException ex) {
                        String errorMsg = messageSource.getMessage("amplifierDefinitionConfig.offset") +
                        messageSource.getMessage("error.amplifierDefinitionConfig.rational");
                        JOptionPane.showMessageDialog(this, errorMsg);
                        return null;
                }

                return new ChannelDefinition(channelno, gain, offset);
        }

        /**
         * Gets the frequencies list
         *
         * @return the frequencies list
         */
        protected JList getDefinitionsList() {

                if (definitionsList == null) {
                        definitionsList = new ChannelDefinitionList();
                }
                return definitionsList;
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
         * Gets the list of frequencies.
         *
         * @return list of frequencies
         */
        protected List<ChannelDefinition> getChannelDefinitions() {

                ArrayList<ChannelDefinition> definitions = new ArrayList<ChannelDefinition>();

                for (int i = 0; i < definitionsList.getModel().getSize(); i++) {

                        ChannelDefinition definition = (ChannelDefinition) definitionsList.getModel().getElementAt(i);
                        definitions.add(definition);
                }

                return definitions;
        }

        /**
         * Gets the channel number
         *
         * @return list of channel numbers
         */
        public List<Integer> getChannelNumbers() {

                ArrayList<Integer> numbers = new ArrayList<Integer>();

                for (ChannelDefinition definition : getChannelDefinitions()) {

                        numbers.add(definition.getNumber());
                }

                return numbers;
        }

        /**
         * Gets the gain values
         *
         * @return list of gain values
         */
        public List<Float> getGainValues() {

                ArrayList<Float> gain = new ArrayList<Float>();

                for (ChannelDefinition definition : getChannelDefinitions()) {

                        gain.add(definition.getGain());
                }

                return gain;
        }

        /**
         * Gets the offset values
         *
         * @return list of channel numbers
         */
        public List<Float> getOffsetValues() {

                ArrayList<Float> offset = new ArrayList<Float>();

                for (ChannelDefinition definition : getChannelDefinitions()) {

                        offset.add(definition.getOffset());
                }

                return offset;
        }

        /**
         * Sets the data
         *
         * @param numbers list od channel numbers
         * @param gainValues list of gain values
         * @param offsetValues list of offset values
         */
        public void setData(List<Integer> numbers, List<Float> gainValues, List<Float> offsetValues) {

                ArrayList<ChannelDefinition> definitions = new ArrayList<ChannelDefinition>();

                for (int i = 0; i < numbers.size(); i++) {

                        ChannelDefinition definition = new ChannelDefinition(numbers.get(i), gainValues.get(i), offsetValues.get(i));
                        definitions.add(definition);
                }

                definitionsList.setListData(definitions.toArray());
        }

        /**
         * Clears text fields.
         */
        public void clearTextFields() {

                channelTextField.setText("");
                gainTextField.setText("");
                offsetTextField.setText("");
        }
}

/**
 * List responsible for showing channel definition objects.
 *
 * @author Tomasz Sawicki
 */
class ChannelDefinitionList extends JList {

        /**
         * Default constructor creates the list.
         */
        public ChannelDefinitionList() {

                super();
                setCellRenderer(new ChannelDefinitionListRenderer());
                setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
}

/**
 * Renderer for a {@link ChannelDefinitionList}.
 *
 * @author Tomasz Sawicki
 */
class ChannelDefinitionListRenderer extends JComponent implements ListCellRenderer {

        /**
         * Renderer showing the number.
         */
        private DefaultListCellRenderer number;

        /**
         * Renderer showing the gain.
         */
        private DefaultListCellRenderer gain;

        /**
         * Renderer showing the offset.
         */
        private DefaultListCellRenderer offset;

        /**
         * Default constructor.
         */
        public ChannelDefinitionListRenderer() {

                number = new DefaultListCellRenderer();
                gain = new DefaultListCellRenderer();
                offset = new DefaultListCellRenderer();

                setLayout(new GridLayout(1, 3));
                add(number);
                add(gain);
                add(offset);
        }

        /**
	 * Returns a component that has been configured to display the specified
	 * value.
	 * @param list the JList we're painting.
	 * @param value the value returned by list.getModel().getElementAt(index).
	 * @param index the cells index.
	 * @param isSelected true if the specified cell was selected.
	 * @param cellHasFocus true if the specified cell has the focus.
	 * @return a component for displaying the specified value
	 */
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                ChannelDefinition definition = (ChannelDefinition) value;
                String noVal = "No: " + definition.getNumber();
                String gainVal = "Gain: " + definition.getGain();
                String offsetVal = "Offset: " + definition.getOffset();

                number.getListCellRendererComponent(list, noVal, index, isSelected, cellHasFocus);
                gain.getListCellRendererComponent(list, gainVal, index, isSelected, cellHasFocus);
                offset.getListCellRendererComponent(list, offsetVal, index, isSelected, cellHasFocus);

                return this;
        }
}