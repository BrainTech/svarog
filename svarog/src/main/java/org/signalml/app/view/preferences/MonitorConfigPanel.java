package org.signalml.app.view.preferences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.app.view.common.components.spinners.IntegerSpinner;

/**
 * Allows to configurate signal recording options.
 *
 * @author Tomasz Sawicki
 */
public class MonitorConfigPanel extends AbstractPanel {

	/**
	 * The frequency text field.
	 */
	private JTextField openbciIpAddressTextField;
	private IntegerSpinner openbciPortSpinner;
	private JCheckBox scrollingModeCheckBox;

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
		add(createOpenbciAddressPanel());
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

		hGroup.addGroup(layout.createParallelGroup().addComponent(getOpenbciIpAddressTextField()).addComponent(getOpenbciPortSpinner()).addComponent(getScrollingModeCheckBox()));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(openbciIpAddressLabel).addComponent(getOpenbciIpAddressTextField()));

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(openbciPortLabel).addComponent(getOpenbciPortSpinner()));

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(getScrollingModeCheckBox()));

		layout.setVerticalGroup(vGroup);

		return openbciAddressPanel;
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

	protected JCheckBox getScrollingModeCheckBox() {
		if (scrollingModeCheckBox == null) {
			scrollingModeCheckBox = new JCheckBox("View in scrolling mode (change will not affect any currently open signals)");
		}
		return scrollingModeCheckBox;
	}

	/**
	 * Fills all the fields of this panel from the given
	 * {@link ApplicationConfiguration configuration} of Svarog.
	 *
	 * @param applicationConfig
	 *            the configuration of Svarog
	 */
	public void fillPanelFromModel(ApplicationConfiguration applicationConfig) {
		getOpenbciIpAddressTextField().setText(applicationConfig.getOpenbciIPAddress());
		getOpenbciPortSpinner().setValue(applicationConfig.getOpenbciPort());
		getScrollingModeCheckBox().setSelected(applicationConfig.isScrollingMode());
	}

	/**
	 * Writes the values of the fields from this panel to the
	 * {@link ApplicationConfiguration configuration} of Svarog.
	 *
	 * @param applicationConfig
	 *            the configuration of Svarog
	 */
	public void fillModelFromPanel(ApplicationConfiguration applicationConfig) {
		applicationConfig.setOpenbciIPAddress(getOpenbciIpAddressTextField().getText());
		applicationConfig.setOpenbciPort(getOpenbciPortSpinner().getValue());
		applicationConfig.setScrollingMode(getScrollingModeCheckBox().isSelected());
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
