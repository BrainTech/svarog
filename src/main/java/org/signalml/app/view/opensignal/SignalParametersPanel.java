package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Panel allowing to present and edit signal parameters.
 *
 * @author Tomasz Sawicki
 */
public class SignalParametersPanel extends JPanel {

	/**
	 * the {@link MessageSourceAccessor source} of messages (labels).
	 */
	private MessageSourceAccessor messageSource;

        /**
         * Current model.
         */
        private Object currentModel;

        /**
	 * the combo box with the sampling frequency.
	 */
	private JComboBox samplingFrequencyComboBox;

	/**
	 * the text field with the number of channels.
	 */
	private JTextField channelCountField;

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
	private JTextField pageSizeField;

	/**
	 * the text field with the number of blocks that fit into one page of
	 * the signal.
	 */
	private JTextField blocksPerPageField;

        /**
         * Button that opens a dialog allowing to edit gain and offset.
         */
        private JButton editGainAndOffsetButton;

        /**
         * Open dialog action.
         */
        private OpenEditGainAndOffsetDialogAction action;

        /**
         * Default constructor. Creates the interface.
         * 
         * @param messageSource {@link #messageSource}
         */
        public SignalParametersPanel(MessageSourceAccessor messageSource) {

                super();
                this.messageSource = messageSource;                
                createInterface();
        }

        /**
         * Fills this panel from a model
         *
         * @param model the model
         * @throws SignalMLException when model is not supported
         */
        public final void fillPanelFromModel(Object model) throws SignalMLException {

                // TODO

                currentModel = model;
        }

        /**
         * Fills a model from this panel.
         *
         * @param model the model
         * @throws SignalMLException when input data is invalid
         */
        public final void fillModelFromPanel(Object model) throws SignalMLException {

                // TODO
        }

        /**
         * Sets enabled to this panel and all it's children.
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

                JLabel samplingFrequencyLabel = new JLabel(messageSource.getMessage("opensignal.parameters.samplingFrequency"));
                JLabel channelCountLabel = new JLabel(messageSource.getMessage("opensignal.parameters.channelCount"));
                JLabel byteOrderLabel = new JLabel(messageSource.getMessage("opensignal.parameters.byteOrder"));
                JLabel sampleTypeLabel = new JLabel(messageSource.getMessage("opensignal.parameters.sampleType"));
                JLabel pageSizeLabel = new JLabel(messageSource.getMessage("opensignal.parameters.pageSize"));
                JLabel blocksPerPageLabel = new JLabel(messageSource.getMessage("opensignal.parameters.blocksPerPage"));

                setBorder(new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("opensignal.signalParametersPanelTitle")),
		        new EmptyBorder(3,3,3,3)
		));

                setLayout(new BorderLayout(0, 10));
                
                JPanel fieldsPanel = new JPanel(new GridBagLayout());

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.CENTER;
                constraints.fill = GridBagConstraints.HORIZONTAL;

                fillConstraints(constraints, 0, 0, 0, 0, 1);
                fieldsPanel.add(samplingFrequencyLabel, constraints);
                fillConstraints(constraints, 1, 0, 1, 0, 1);
                fieldsPanel.add(createWrapperPanel(getSamplingFrequencyComboBox(), 8), constraints);

                fillConstraints(constraints, 0, 1, 0, 0, 1);
                fieldsPanel.add(channelCountLabel, constraints);
                fillConstraints(constraints, 1, 1, 1, 0, 1);
                fieldsPanel.add(createWrapperPanel(getChannelCountField(), 8), constraints);

                fillConstraints(constraints, 0, 2, 0, 0, 1);
                fieldsPanel.add(byteOrderLabel, constraints);
                fillConstraints(constraints, 1, 2, 1, 0, 1);
                fieldsPanel.add(createWrapperPanel(getByteOrderComboBox(), 8), constraints);

                fillConstraints(constraints, 0, 3, 0, 0, 1);
                fieldsPanel.add(sampleTypeLabel, constraints);
                fillConstraints(constraints, 1, 3, 1, 0, 1);
                fieldsPanel.add(createWrapperPanel(getSampleTypeComboBox(), 8), constraints);

                fillConstraints(constraints, 0, 4, 0, 0, 1);
                fieldsPanel.add(pageSizeLabel, constraints);
                fillConstraints(constraints, 1, 4, 1, 0, 1);
                fieldsPanel.add(createWrapperPanel(getPageSizeField(), 8), constraints);

                fillConstraints(constraints, 0, 5, 0, 0, 1);
                fieldsPanel.add(blocksPerPageLabel, constraints);
                fillConstraints(constraints, 1, 5, 1, 0, 1);
                fieldsPanel.add(createWrapperPanel(getBlocksPerPageField(), 8), constraints);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.add(getEditGainAndOffsetButton());

                add(fieldsPanel, BorderLayout.NORTH);
                add(buttonPanel, BorderLayout.SOUTH);

                setEnabledAll(false);
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
        private void fillConstraints(GridBagConstraints constraints, int gridx, int gridy, int weightx, int weighty, int gridwidth) {
                
                constraints.gridx = gridx;
                constraints.gridy = gridy;
                constraints.weightx = weightx;
                constraints.weighty = weighty;
                constraints.gridwidth = gridwidth;
        }

        /**
         * Creates a wrapper panel with an empty border.
         * Inside component is stretched horizontaly.
         *
         * @param component inside component
         * @param border border size
         * @return the wrapper panel
         */
        private JPanel createWrapperPanel(JComponent component, int border) {

                JPanel wrapperPanel = new JPanel(new GridBagLayout());
                wrapperPanel.setBorder(new EmptyBorder(border, border, border, border));

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.CENTER;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                fillConstraints(constraints, 0, 0, 1, 1, 1);

                wrapperPanel.add(component, constraints);

                return wrapperPanel;
        }

        /**
	 * Returns the sampling frequency combo box.
         *
         * @return the sampling frequency combo box
	 */
	public JComboBox getSamplingFrequencyComboBox() {
		if (samplingFrequencyComboBox == null) {
			samplingFrequencyComboBox = new JComboBox();
		}
		return samplingFrequencyComboBox;
	}

	/**
	 * Returns the channel count field
         *
         * @return the channel count field
	 */
	public JTextField getChannelCountField() {
		if (channelCountField == null) {
			channelCountField = new JTextField();
		}
		return channelCountField;
	}

	/**
	 * Returns the byte order combo box.
         *
         * @return the byte order combo box
	 */
	public ResolvableComboBox getByteOrderComboBox() {

                if (byteOrderComboBox == null) {
			byteOrderComboBox = new ResolvableComboBox(messageSource);			
		}
		return byteOrderComboBox;
	}

	/**
	 * Returns the sample type combo box.
         *
         * @return the sample type combo box
	 */
        public ResolvableComboBox getSampleTypeComboBox() {

                if (sampleTypeComboBox == null) {
			sampleTypeComboBox = new ResolvableComboBox(messageSource);			
		}
		return sampleTypeComboBox;
	}

	/**
	 * Returns the page size field.
         *
         * @return the page size field
	 */
	public JTextField getPageSizeField() {
		if (pageSizeField == null) {
			pageSizeField = new JTextField();
		}
		return pageSizeField;
	}

	/**
	 * Returns the blocks per page field.
         *
         * @return the blocks per page field
	 */
	public JTextField getBlocksPerPageField() {
		if (blocksPerPageField == null) {
			blocksPerPageField = new JTextField();
		}
		return blocksPerPageField;
	}

        /**
         * Returns the edit gain and offset button.
         *
         * @return the edit gain and offset button
         */
        public JButton getEditGainAndOffsetButton() {
                if (editGainAndOffsetButton == null) {
                        editGainAndOffsetButton = new JButton(getOpenEditGainAndOffsetDialogAction());
                        editGainAndOffsetButton.setText(messageSource.getMessage("opensignal.parameters.editGainAndOffset"));
                }
                return editGainAndOffsetButton;
        }

        /**
         * Returns the edit gain and offset dialog action
         *
         * @return the action
         */
        private OpenEditGainAndOffsetDialogAction getOpenEditGainAndOffsetDialogAction() {
                
                if (action == null) {
                        action = new OpenEditGainAndOffsetDialogAction(messageSource, currentModel);
                }
                return action;
        }

        /**
         * Open EditGainAndOffsetDialog action.
         */
        private class OpenEditGainAndOffsetDialogAction extends AbstractAction {

                /**
                 * Edit gain and offset dialog.
                 */
                private EditGainAndOffsetDialog dialog;

                /**
                 * Signal parameters descriptor.
                 */
                private Object currentModel;

                /**
                 * Default construcot.
                 *
                 * @param messageSource the message source
                 */
                public OpenEditGainAndOffsetDialogAction(MessageSourceAccessor messageSource, Object model) {

                        this.dialog = new EditGainAndOffsetDialog(messageSource, null, true);
                        this.currentModel = model;
                }

                /**
                 * Shows the dialog.
                 *
                 * @param e action event
                 */
                @Override
                public void actionPerformed(ActionEvent e) {

                        dialog.showDialog(currentModel);
                }
        }
}