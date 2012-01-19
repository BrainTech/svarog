package org.signalml.app.view.document.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Allows to add available frequencies.
 *
 * @author Tomasz Sawicki
 */
public class AvailableFrequenciesPanel extends JPanel implements ActionListener {

        /**
         * List of frequencies.
         */
        private JList frequenciesList;

        /**
         * Insert text field.
         */
        private JTextField textField;

        /**
         * Insert button.
         */
        private JButton addButton;

        /**
         * Remove button.
         */
        private JButton removeButton;

        /**
         * Default constructor.
         */
        public AvailableFrequenciesPanel() {

                super();

                CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Available frequencies")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

                setLayout(new BorderLayout(10, 3));

                add(new JScrollPane(getFrequenciesList()), BorderLayout.CENTER);

                JPanel bottomPanel = new JPanel(new GridBagLayout());
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridy = 0;
                constraints.anchor = GridBagConstraints.CENTER;
                constraints.fill = GridBagConstraints.HORIZONTAL;

                constraints.gridx = 0;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                bottomPanel.add(getTextField(), constraints);

                constraints.gridx = 2;
                constraints.gridwidth = 1;
                constraints.weightx = 0;

                JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonsPanel.add(getAddButton());
                buttonsPanel.add(getRemoveButton());

                bottomPanel.add(buttonsPanel, constraints);

                add(bottomPanel, BorderLayout.PAGE_END);
        }

        /**
         * Gets the frequencies list
         *
         * @return the frequencies list
         */
        private JList getFrequenciesList() {
                
                if (frequenciesList == null) {
                        frequenciesList = new JList();
                        frequenciesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                }
                return frequenciesList;
        }

        /**
         * Gets the add button.
         *
         * @return the add button
         */
        private JButton getAddButton() {

                if (addButton == null) {
                        addButton = new JButton();
                        addButton.setText(_("Add"));
                        addButton.addActionListener(this);
                }
                return addButton;
        }

        /**
         * Gets the remove button.
         *
         * @return the remove button
         */
        private JButton getRemoveButton() {

                if (removeButton == null) {
                        removeButton = new JButton();
                        removeButton.setText(_("Remove"));
                        removeButton.addActionListener(this);
                }
                return removeButton;
        }

        /**
         * Gets the text field
         *
         * @return the text field
         */
        private JTextField getTextField() {
                
                if (textField == null) {
                        textField = new JTextField();                        
                }
                return textField;
        }

        /**
         * Called when buttons are clicked.
         * 
         * @param e action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {

                if (addButton.equals(e.getSource())) {

                        Float result;

                        try {
                                result = Float.parseFloat(textField.getText());
                        } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(this, _("Please insert an integer value"));
                                return;
                        }

                        List<Float> frequencies = getFrequencies();
                        frequencies.add(result);
                        frequenciesList.setListData(frequencies.toArray());

                        clearTextField();
                }
                else if (removeButton.equals(e.getSource())) {

                        int selectedIndex = frequenciesList.getSelectedIndex();

                        if (selectedIndex == -1 )
                                return;

                        List<Float> frequencies = getFrequencies();
                        frequencies.remove(selectedIndex);
                        frequenciesList.setListData(frequencies.toArray());

                        frequenciesList.setSelectedIndex(-1);
                }
        }

        /**
         * Gets the list of frequencies.
         *
         * @return list of frequencies
         */
        public List<Float> getFrequencies() {

                ArrayList<Float> frequencies = new ArrayList<Float>();

                for (int i = 0; i < frequenciesList.getModel().getSize(); i++) {

                        Float frequency = (Float) frequenciesList.getModel().getElementAt(i);
                        frequencies.add(frequency);
                }

                return frequencies;
        }

        /**
         * Sets the list of frequencies.
         *
         * @param frequencies list of frequencies
         */
        public void setFrequencies(List<Float> frequencies) {

                frequenciesList.setListData(frequencies.toArray());
        }

        /**
         * Clears the text field.
         */
        public void clearTextField() {
                textField.setText("");
        }
}