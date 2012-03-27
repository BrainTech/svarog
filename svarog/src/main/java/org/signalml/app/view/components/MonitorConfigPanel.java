package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.components.validation.ValidationErrors;

/**
 * Allows to configurate signal recording options.
 * 
 * @author Tomasz Sawicki
 */
public class MonitorConfigPanel extends AbstractPanel {

	/**
	 * The frequency text field.
	 */
	private FloatSpinner backupFrequencySpinner;

	private JTextField openbciIpAddressTextField;
	private IntegerSpinner openbciPortSpinner;

	/**
	 * Default constructor.
	 */
	public MonitorConfigPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel.
	 */
	private void initialize() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(createSignalRecordingPanel());
		add(createOpenbciAddressPanel());
	}

	protected JPanel createSignalRecordingPanel() {
		JPanel signalRecordingPanel = new JPanel();

		JLabel backupFrequencyLabel = new JLabel(_("Backup frequency [s]"));

		signalRecordingPanel.setLayout(new GridBagLayout());
		setTitledBorder(signalRecordingPanel, _("Signal recording"));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(3, 3, 3, 3);
		constraints.weighty = 0;

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.weightx = 0;
		signalRecordingPanel.add(backupFrequencyLabel, constraints);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.weightx = 1;
		signalRecordingPanel.add(getBackupFrequencySpinner(), constraints);

		return signalRecordingPanel;
	}

	protected JPanel createOpenbciAddressPanel() {
		JPanel openbciAddressPanel = new JPanel();
		setTitledBorder(openbciAddressPanel, _("OpenBCI Address"));

		JLabel openbciIpAddressLabel = new JLabel(_("OpenBCI IP Address"));
		JLabel openbciPortLabel = new JLabel(_("OpenBCI port"));

		GroupLayout layout = new GroupLayout(openbciAddressPanel);
		openbciAddressPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(openbciIpAddressLabel).addComponent(openbciPortLabel));

		hGroup.addGroup(layout.createParallelGroup().addComponent(getOpenbciIpAddressTextField()).addComponent(getOpenbciPortSpinner()));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(openbciIpAddressLabel).addComponent(getOpenbciIpAddressTextField()));

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(openbciPortLabel).addComponent(getOpenbciPortSpinner()));

		layout.setVerticalGroup(vGroup);

		return openbciAddressPanel;
	}

	/**
	 * Returns the frequency text field.
	 * 
	 * @return the frequency text field
	 */
	protected FloatSpinner getBackupFrequencySpinner() {
		if (backupFrequencySpinner == null) {
			backupFrequencySpinner = new FloatSpinner(new SpinnerNumberModel(10.0, 0.1, 100000.0, 0.1));
		}
		return backupFrequencySpinner;
	}

	protected JTextField getOpenbciIpAddressTextField() {
		if (openbciIpAddressTextField == null)
			openbciIpAddressTextField = new JTextField();
		return openbciIpAddressTextField;
	}

	protected IntegerSpinner getOpenbciPortSpinner() {
		if (openbciPortSpinner == null) {
			openbciPortSpinner = new IntegerSpinner(new SpinnerNumberModel(1, 1, 99999, 1));
			NumberEditor editor = (NumberEditor) openbciPortSpinner.getEditor();
			editor.getFormat().setGroupingUsed(false);
		}
		return openbciPortSpinner;
	}

	/**
	 * Fills all the fields of this panel from the given
	 * {@link ApplicationConfiguration configuration} of Svarog.
	 * 
	 * @param applicationConfig
	 *            the configuration of Svarog
	 */
	public void fillPanelFromModel(ApplicationConfiguration applicationConfig) {
		getBackupFrequencySpinner().setValue(applicationConfig.getBackupFrequency());
		getOpenbciIpAddressTextField().setText(applicationConfig.getOpenbciIPAddress());
		getOpenbciPortSpinner().setValue(applicationConfig.getOpenbciPort());
	}

	/**
	 * Writes the values of the fields from this panel to the
	 * {@link ApplicationConfiguration configuration} of Svarog.
	 * 
	 * @param applicationConfig
	 *            the configuration of Svarog
	 */
	public void fillModelFromPanel(ApplicationConfiguration applicationConfig) {
		applicationConfig.setBackupFrequency(getBackupFrequencySpinner().getValue());
		applicationConfig.setOpenbciIPAddress(getOpenbciIpAddressTextField().getText());
		applicationConfig.setOpenbciPort(openbciPortSpinner.getValue());
	}

	/**
	 * Validates this panel.
	 * 
	 * @param errors
	 *            the object in which the errors should be stored
	 */
	public void validate(ValidationErrors errors) {

		String ipAddressPattern = 
				"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." 
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." 
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." 
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		
		Pattern pattern = Pattern.compile(ipAddressPattern);
		Matcher matcher = pattern.matcher(getOpenbciIpAddressTextField().getText());

		if (!matcher.matches())
			errors.addError(_("Bad IP address value!"));

	}
}
