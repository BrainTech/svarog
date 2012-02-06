package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.components.validation.ValidationErrors;

import org.springframework.validation.Errors;

/**
 * Allows to configurate signal recording options.
 *
 * @author Tomasz Sawicki
 */
public class SignalRecordingConfigPanel extends JPanel {

        /**
         * Frequency label.
         */
        private JLabel frequencyLabel;
        /**
         * The frequency text field.
         */
        private JTextField frequencyTextField;

        /**
         * Default constructor.
         */
        public SignalRecordingConfigPanel() {
                super();
                initialize();
        }

        /**
         * Initializes this panel.
         */
        private void initialize() {
                setBorder(new EmptyBorder(3,3,3,3));
		setLayout(new BorderLayout());

                JPanel generalPanel = new JPanel();
                
                generalPanel.setLayout(new GridBagLayout());
                generalPanel.setBorder(new CompoundBorder(
                        new TitledBorder(_("General")),
                        new EmptyBorder(3,3,3,3)
                ));

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.CENTER;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(3, 3, 3, 3);
                constraints.weighty = 0;

                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.gridwidth = 1;
                constraints.weightx = 0;
                generalPanel.add(getFrequencyLabel(), constraints);

                constraints.gridx = 1;
                constraints.gridy = 0;
                constraints.gridwidth = 2;
                constraints.weightx = 1;
                generalPanel.add(getFrequencyTextField(), constraints);

                add(generalPanel, BorderLayout.NORTH);
        }

        /**
         * Returns the frequency label.
         * @return the frequency label
         */
        public JLabel getFrequencyLabel() {
                if (frequencyLabel == null) {
                        frequencyLabel = new JLabel();
                        frequencyLabel.setText(_("Backup frequency [s]"));
                }
                return frequencyLabel;
        }

        /**
         * Returns the frequency text field.
         * @return the frequency text field
         */
        public JTextField getFrequencyTextField() {
                if (frequencyTextField == null) {
                        frequencyTextField = new JTextField();
                }
                return frequencyTextField;
        }

        /**
	 * Fills all the fields of this panel from the given
	 * {@link ApplicationConfiguration configuration} of Svarog.
	 * @param applicationConfig the configuration of Svarog
	 */
	public void fillPanelFromModel(ApplicationConfiguration applicationConfig) {
                frequencyTextField.setText(Float.toString(applicationConfig.getBackupFrequency()));
        }

	/**
	 * Writes the values of the fields from this panel to the
	 * {@link ApplicationConfiguration configuration} of Svarog.
	 * @param applicationConfig the configuration of Svarog
	 */
	public void fillModelFromPanel(ApplicationConfiguration applicationConfig) {
                applicationConfig.setBackupFrequency(Float.parseFloat(frequencyTextField.getText()));
        }

	/**
	 * Validates this panel.
	 * @param errors the object in which the errors should be stored
	 */
	public void validate(ValidationErrors errors) {
                try {
			Float.parseFloat(getFrequencyTextField().getText());
		} catch (NumberFormatException ex) {
			errors.addError(_("Invalid numeric value"));
		}
	}
}
