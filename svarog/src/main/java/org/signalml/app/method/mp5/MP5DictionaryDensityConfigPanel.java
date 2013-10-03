/* MP5DictionaryDensityConfigPanel.java created 2008-01-30
 *
 */
package org.signalml.app.method.mp5;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.common.components.CompactButton;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.method.mp5.MP5Parameters;

import org.springframework.validation.Errors;

/** MP5DictionaryDensityConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5DictionaryDensityConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AbstractDialog owner;

	private JSpinner energyErrorSpinner;
	private JSpinner energyErrorPercentageSpinner;
	private JTextField atomCountTextField;
	private JTextField ramUsageTextField;

	public MP5DictionaryDensityConfigPanel(AbstractDialog owner) {
		super();
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Dictionary density and size")),
			new EmptyBorder(3,3,3,3)
		);

		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel energyErrorLabel = new JLabel(_("Energy error"));
		JLabel energyErrorPercentageLabel = new JLabel(_("Energy error percentage chosen [%]"));
		JLabel atomCountLabel = new JLabel(_("Atoms in dictionary"));
		JLabel ramUsageLabel = new JLabel(_("Approximate RAM usage"));

		Component glue1 = Box.createHorizontalGlue();
		Component glue2 = Box.createHorizontalGlue();
		Component glue3 = Box.createHorizontalGlue();
		Component glue4 = Box.createHorizontalGlue();

//		CompactButton energyErrorHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_ENERGY_ERROR);
//		CompactButton atomCountHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_ATOM_COUNT);
//		CompactButton ramUsageHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_RAM_USAGE);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(energyErrorLabel)
			.addComponent(energyErrorPercentageLabel)
			.addComponent(atomCountLabel)
			.addComponent(ramUsageLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(glue1)
			.addComponent(glue2)
			.addComponent(glue3)
			.addComponent(glue4)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getEnergyErrorSpinner())
			.addComponent(getEnergyErrorPercentageSpinner())
			.addComponent(getAtomCountTextField())
			.addComponent(getRamUsageTextField())
		);

//		hGroup.addGroup(
//			layout.createParallelGroup()
//			.addComponent(energyErrorHelpButton)
//			.addComponent(energyErrorHelpButton)
//			.addComponent(atomCountHelpButton)
//			.addComponent(ramUsageHelpButton)
//		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.CENTER)
			.addComponent(energyErrorLabel)
			.addComponent(glue1)
			.addComponent(getEnergyErrorSpinner())
//			.addComponent(energyErrorHelpButton)
		);

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.CENTER)
			.addComponent(energyErrorPercentageLabel)
			.addComponent(glue2)
			.addComponent(getEnergyErrorPercentageSpinner())
//			.addComponent(energyErrorHelpButton)
		);

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.CENTER)
			.addComponent(atomCountLabel)
			.addComponent(glue3)
			.addComponent(getAtomCountTextField())
//			.addComponent(atomCountHelpButton)
		);

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.CENTER)
			.addComponent(ramUsageLabel)
			.addComponent(glue4)
			.addComponent(getRamUsageTextField())
//			.addComponent(ramUsageHelpButton)
		);

		layout.setVerticalGroup(vGroup);

	}

	@SuppressWarnings("cast")
	public JSpinner getEnergyErrorSpinner() {
		if (energyErrorSpinner == null) {
			energyErrorSpinner = new JSpinner(
				new SpinnerNumberModel(
					((double) MP5Parameters.MIN_ENERGY_ERROR),
					((double) MP5Parameters.MIN_ENERGY_ERROR),
					((double) MP5Parameters.MAX_ENERGY_ERROR),
					0.1d
				)
			);
			energyErrorSpinner.setPreferredSize(MP5MethodDialog.FIELD_SIZE);
			energyErrorSpinner.setMaximumSize(MP5MethodDialog.FIELD_SIZE);
			energyErrorSpinner.setMinimumSize(MP5MethodDialog.FIELD_SIZE);
		}
		return energyErrorSpinner;
	}

	@SuppressWarnings("cast")
	public JSpinner getEnergyErrorPercentageSpinner() {
		if (energyErrorPercentageSpinner == null) {
			energyErrorPercentageSpinner = new JSpinner(
				new SpinnerNumberModel(
					((double) MP5Parameters.MIN_ENERGY_ERROR_PERCENTAGE),
					((double) MP5Parameters.MIN_ENERGY_ERROR_PERCENTAGE),
					((double) MP5Parameters.MAX_ENERGY_ERROR_PERCENTAGE),
					0.1d
				)
			);
			energyErrorPercentageSpinner.setPreferredSize(MP5MethodDialog.FIELD_SIZE);
			energyErrorPercentageSpinner.setMaximumSize(MP5MethodDialog.FIELD_SIZE);
			energyErrorPercentageSpinner.setMinimumSize(MP5MethodDialog.FIELD_SIZE);
		}
		return energyErrorPercentageSpinner;
	}

	public JTextField getAtomCountTextField() {
		if (atomCountTextField == null) {
			atomCountTextField = new JTextField();
			atomCountTextField.setPreferredSize(MP5MethodDialog.FIELD_SIZE);
			atomCountTextField.setMaximumSize(MP5MethodDialog.FIELD_SIZE);
			atomCountTextField.setMinimumSize(MP5MethodDialog.FIELD_SIZE);
			atomCountTextField.setHorizontalAlignment(JTextField.RIGHT);
			atomCountTextField.setEditable(false);
		}
		return atomCountTextField;
	}

	public JTextField getRamUsageTextField() {
		if (ramUsageTextField == null) {
			ramUsageTextField = new JTextField();
			ramUsageTextField.setPreferredSize(MP5MethodDialog.FIELD_SIZE);
			ramUsageTextField.setMaximumSize(MP5MethodDialog.FIELD_SIZE);
			ramUsageTextField.setMinimumSize(MP5MethodDialog.FIELD_SIZE);
			ramUsageTextField.setHorizontalAlignment(JTextField.RIGHT);
			ramUsageTextField.setEditable(false);
		}
		return ramUsageTextField;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {

		getEnergyErrorSpinner().setValue(new Double(parameters.getEnergyError()));
		getEnergyErrorPercentageSpinner().setValue(new Double(parameters.getEnergyErrorPercentage()));

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		parameters.setEnergyError(((Number) getEnergyErrorSpinner().getValue()).floatValue());
		parameters.setEnergyErrorPercentage(((Number) getEnergyErrorPercentageSpinner().getValue()).floatValue());

	}

	public void validatePanel(ValidationErrors errors) {

		// nothing to do

	}

}
