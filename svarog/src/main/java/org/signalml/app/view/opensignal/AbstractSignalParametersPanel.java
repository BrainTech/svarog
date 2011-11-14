package org.signalml.app.view.opensignal;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.signalml.app.view.element.IntegerSpinner;
import org.signalml.app.view.element.FloatSpinner;
import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.plugin.export.SignalMLException;

/**
 * Panel allowing to present and edit signal parameters.
 *
 * @author Tomasz Sawicki
 */
public abstract class AbstractSignalParametersPanel extends JPanel {

        public static String NUMBER_OF_CHANNELS_PROPERTY = "numberOfChannelsChangedProperty";
        public static String SAMPLING_FREQUENCY_PROPERTY = "samplingFrequencyChanged";
	public static String CHANNEL_LABELS_PROPERTY = "channelLabelsPropertyChanged";

        /**
         * Current model.
         */
        protected Object currentModel;
        /**
         * the combo box with the sampling frequency.
         */
        private JComboBox samplingFrequencyComboBox;
        /**
         * the text field with the number of channels.
         */
        private IntegerSpinner channelCountSpinner;
        /**
         * A text field allwing to change byte order.
         */
        private ResolvableComboBox byteOrderComboBox;
        /**
         * A text field allwing to change the sample type.
         */
        private ResolvableComboBox sampleTypeComboBox;
        /**
         * the text field with the size of the page of signal in seconds.
         */
        private FloatSpinner pageSizeSpinner;
        /**
         * the text field with the number of blocks that fit into one page of
         * the signal.
         */
        private IntegerSpinner blocksPerPageSpinner;
        /**
         * Button that opens a dialog allowing to edit gain and offset.
         */
        private JButton editGainAndOffsetButton;
        /**
         * Edit gain and offset dialog.
         */
        private EditGainAndOffsetDialog editGainAndOffsetDialog;

        /**
         * Default constructor. Creates the interface.
         *
         */
        public  AbstractSignalParametersPanel() {

                super();
                createInterface();

		getSamplingFrequencyComboBox().setEditable(true);
		getSamplingFrequencyComboBox().setSelectedItem(1024.0F);
        }

        /**
         * Fills {@link #currentModel} from this panel.
         *
         * @throws SignalMLException when input data is invalid
         */
        protected abstract void fillCurrentModelFromPanel() throws SignalMLException;

        /**
         * Sets enabled to this panel and all it's children.
         * Clears all fields if enabled == false.
         *
         * @param enabled true or false
         */
        public void setEnabledAll(boolean enabled) {

                setEnabledToChildren(this, enabled);
        }

        /**
         * Sets enabled to a component and all of it's children.
         *
         * @param component target component
         * @param enabled true or false
         * @param omit wheter to omit component
         */
        private void setEnabledToChildren(Component component, boolean enabled) {

                component.setEnabled(enabled);
                if (component instanceof Container) {
                        Component[] children = ((Container) component).getComponents();
                        for (Component child : children) {
                                setEnabledToChildren(child, enabled);
                        }
                }
        }

        /**
         * Creates the interface.
         */
        private void createInterface() {


                JPanel buttonPanel = createButtonPanel();
		JPanel fieldsPanel = createFieldsPanel();

                add(fieldsPanel, BorderLayout.NORTH);
                add(buttonPanel, BorderLayout.SOUTH);

                setEnabledAll(false);
        }

        /**
         * Fills a {@link JPanel} object with component, regarding {@link GridBagConstraints} object.
         *
         * @param fieldsPanel an object to be filled with components
         * @param constraints an object regarding to which components should be placed in fieldsPanel
         * @param startingRow a row to start from

         * @return a number of rows that fieldsPanel contains after performing all operations
         */

	protected int createFieldsPanelElements(JPanel fieldsPanel, GridBagConstraints constraints, int startingRow) {
                JLabel samplingFrequencyLabel = new JLabel(_("Sampling frequency: "));
                JLabel channelCountLabel = new JLabel(_("Channel count: "));
                JLabel byteOrderLabel = new JLabel(_("Byte order: "));
                JLabel sampleTypeLabel = new JLabel(_("Sample type: "));
                JLabel pageSizeLabel = new JLabel(_("Page size: "));
                JLabel blocksPerPageLabel = new JLabel(_("Number of blocks per page: "));

		int row = startingRow;
                fillConstraints(constraints, 0, row, 0, 0, 1);
                fieldsPanel.add(samplingFrequencyLabel, constraints);
                fillConstraints(constraints, 1, row, 1, 0, 1);
                fieldsPanel.add(getSamplingFrequencyComboBox(), constraints);
		row++;

                fillConstraints(constraints, 0, row, 0, 0, 1);
                fieldsPanel.add(channelCountLabel, constraints);
                fillConstraints(constraints, 1, row, 1, 0, 1);
                fieldsPanel.add(getChannelCountSpinner(), constraints);
		row++;

                fillConstraints(constraints, 0, row, 0, 0, 1);
                fieldsPanel.add(byteOrderLabel, constraints);
                fillConstraints(constraints, 1, row, 1, 0, 1);
                fieldsPanel.add(getByteOrderComboBox(), constraints);
		row++;

                fillConstraints(constraints, 0, row, 0, 0, 1);
                fieldsPanel.add(sampleTypeLabel, constraints);
                fillConstraints(constraints, 1, row, 1, 0, 1);
                fieldsPanel.add(getSampleTypeComboBox(), constraints);
		row++;

                fillConstraints(constraints, 0, row, 0, 0, 1);
                fieldsPanel.add(pageSizeLabel, constraints);
                fillConstraints(constraints, 1, row, 1, 0, 1);
                fieldsPanel.add(getPageSizeSpinner(), constraints);
		row++;

                fillConstraints(constraints, 0, row, 0, 0, 1);
                fieldsPanel.add(blocksPerPageLabel, constraints);
                fillConstraints(constraints, 1, row, 1, 0, 1);
                fieldsPanel.add(getBlocksPerPageSpinner(), constraints);
		row++;
		return row;

	}

	protected JPanel createFieldsPanel() {

                setBorder(new CompoundBorder(
                        new TitledBorder(_("Signal parameters")),
                        new EmptyBorder(3, 3, 3, 3)));

                setLayout(new BorderLayout(0, 10));

                JPanel fieldsPanel = new JPanel(new GridBagLayout());

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.CENTER;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(8, 8, 8, 8);

		this.createFieldsPanelElements(fieldsPanel, constraints, 0);

		return fieldsPanel;

	}

        protected JPanel createButtonPanel() {
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.add(getEditGainAndOffsetButton());
                return buttonPanel;
        }

        /**
         * Method fills a {@link GridBagConstraints} object with data.
         *
         * @param constraints a {@link GridBagConstraints}
         * @param gridx {@link GridBagConstraints#gridx}
         * @param gridy {@link GridBagConstraints#gridy}
         * @param weightx {@link GridBagConstraints#weightx}
         * @param weighty {@link GridBagConstraints#weighty}
         * @param gridwidth {@link GridBagConstraints#gridwidth}
         */
        protected void fillConstraints(GridBagConstraints constraints, int gridx, int gridy, int weightx, int weighty, int gridwidth) {

                constraints.gridx = gridx;
                constraints.gridy = gridy;
                constraints.weightx = weightx;
                constraints.weighty = weighty;
                constraints.gridwidth = gridwidth;
        }

        /**
         * Returns the sampling frequency combo box.
         *
         * @return the sampling frequency combo box
         */
        protected JComboBox getSamplingFrequencyComboBox() {

                if (samplingFrequencyComboBox == null) {
                        samplingFrequencyComboBox = new JComboBox();
                        samplingFrequencyComboBox.addActionListener(new ActionListener() {

                                private float previousSamplingFrequency = -1;

                                @Override
                                public void actionPerformed(ActionEvent e) {

                                        String selectedItemString = samplingFrequencyComboBox.getSelectedItem().toString();

                                        if (!selectedItemString.isEmpty()) {
                                                float currentSamplingFrequency = Float.parseFloat(selectedItemString);
                                                if (currentSamplingFrequency != previousSamplingFrequency) {
							fireSamplingFrequencyChanged(previousSamplingFrequency, currentSamplingFrequency);
							previousSamplingFrequency = currentSamplingFrequency;
                                                }
                                        }
                                }
                        });
                }
                return samplingFrequencyComboBox;
        }

	/**
	 * Notifies all listeners that the sampling frequency has changed.
	 * @param previousSamplingFrequency the old sampling frequency
	 * @param currentSamplingFrequency the new sampling frequency
	 */
	protected void fireSamplingFrequencyChanged(double previousSamplingFrequency, double currentSamplingFrequency) {
		firePropertyChange(SAMPLING_FREQUENCY_PROPERTY, previousSamplingFrequency, currentSamplingFrequency);
	}

        /**
         * Returns the channel count field
         *
         * @return the channel count field
         */
        protected IntegerSpinner getChannelCountSpinner() {

                if (channelCountSpinner == null) {
                        channelCountSpinner = new IntegerSpinner(new SpinnerNumberModel(4, 1, 50, 1));
                        channelCountSpinner.addChangeListener(new ChangeListener() {

                                @Override
                                public void stateChanged(ChangeEvent e) {
                                        int numberOfChannels = getChannelCountSpinner().getValue();
                                        fireNumberOfChannelsChanged(numberOfChannels);
                                }
                        });

                }
                return channelCountSpinner;
        }

	/**
	 * Notifies all listeners that the number of channels has changed.
	 * @param numberOfChannels the new number of channels
	 */
	protected void fireNumberOfChannelsChanged(int numberOfChannels) {
		firePropertyChange(NUMBER_OF_CHANNELS_PROPERTY, 0, numberOfChannels);
	}

        /**
         * Returns the byte order combo box.
         *
         * @return the byte order combo box
         */
        protected ResolvableComboBox getByteOrderComboBox() {

                if (byteOrderComboBox == null) {
                        byteOrderComboBox = new ResolvableComboBox();
                        byteOrderComboBox.setModel(new DefaultComboBoxModel(RawSignalByteOrder.values()));
                }
                return byteOrderComboBox;
        }

        /**
         * Returns the sample type combo box.
         *
         * @return the sample type combo box
         */
        protected ResolvableComboBox getSampleTypeComboBox() {

                if (sampleTypeComboBox == null) {
                        sampleTypeComboBox = new ResolvableComboBox();
                        sampleTypeComboBox.setModel(new DefaultComboBoxModel(RawSignalSampleType.values()));
                }
                return sampleTypeComboBox;
        }

        /**
         * Returns the page size spinner.
         *
         * @return the page size spinner
         */
        protected FloatSpinner getPageSizeSpinner() {

                if (pageSizeSpinner == null) {
                        pageSizeSpinner = new FloatSpinner(new SpinnerNumberModel(20.0F, 0.1F, 100000.0F, 0.1F));
                }
                return pageSizeSpinner;
        }

        /**
         * Returns the blocks per page field.
         *
         * @return the blocks per page field
         */
        protected IntegerSpinner getBlocksPerPageSpinner() {

                if (blocksPerPageSpinner == null) {
                        blocksPerPageSpinner = new IntegerSpinner(new SpinnerNumberModel(4, 1, 200, 1));
                }
                return blocksPerPageSpinner;
        }

        /**
         * Returns the edit gain and offset button.
         *
         * @return the edit gain and offset button
         */
        protected JButton getEditGainAndOffsetButton() {

                if (editGainAndOffsetButton == null) {
                        editGainAndOffsetButton = new JButton(new AbstractAction() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        try {
                                                fillCurrentModelFromPanel();
                                                getEditGainAndOffsetDialog().showDialog(currentModel, true);
                                        } catch (SignalMLException ex) {
                                                JOptionPane.showMessageDialog(null, ex.getMessage(), _("Error!"), JOptionPane.ERROR_MESSAGE);
                                        }
                                }
                        });
                        editGainAndOffsetButton.setText(_("Edit gain and offset"));
                }
                return editGainAndOffsetButton;
        }

        /**
         * Returns the edit gain and offset dialog
         *
         * @return the edit gain and offset dialog
         */
        protected EditGainAndOffsetDialog getEditGainAndOffsetDialog() {

                if (editGainAndOffsetDialog == null) {
                        editGainAndOffsetDialog = new EditGainAndOffsetDialog( null, true);
                }
                return editGainAndOffsetDialog;
        }


	public int getChannelCount() {
		return getChannelCountSpinner().getValue();
	}

	public float getSamplingFrequency() {
		return Float.parseFloat(getSamplingFrequencyComboBox().getSelectedItem().toString());
	}

}
