/* MP5DictionaryDensityConfigPanel.java created 2008-01-30
 *
 */
package org.signalml.app.method.mp5;

import java.awt.Component;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.method.mp5.MP5Parameters;

/** MP5DictionaryDensityConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5DictionaryDensityConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AbstractDialog owner;

	private JSpinner energyErrorSpinner;
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
		JLabel atomCountLabel = new JLabel(_("Atoms in dictionary"));
		JLabel ramUsageLabel = new JLabel(_("Approximate RAM usage"));

		Component glue1 = Box.createHorizontalGlue();
		Component glue2 = Box.createHorizontalGlue();
		Component glue3 = Box.createHorizontalGlue();

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(energyErrorLabel)
			.addComponent(atomCountLabel)
			.addComponent(ramUsageLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(glue1)
			.addComponent(glue2)
			.addComponent(glue3)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getEnergyErrorSpinner())
			.addComponent(getAtomCountTextField())
			.addComponent(getRamUsageTextField())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.CENTER)
			.addComponent(energyErrorLabel)
			.addComponent(glue1)
			.addComponent(getEnergyErrorSpinner())
		);

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.CENTER)
			.addComponent(atomCountLabel)
			.addComponent(glue2)
			.addComponent(getAtomCountTextField())
		);

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.CENTER)
			.addComponent(ramUsageLabel)
			.addComponent(glue3)
			.addComponent(getRamUsageTextField())
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
					0.01d
				)
			);
			energyErrorSpinner.setPreferredSize(MP5MethodDialog.FIELD_SIZE);
			energyErrorSpinner.setMaximumSize(MP5MethodDialog.FIELD_SIZE);
			energyErrorSpinner.setMinimumSize(MP5MethodDialog.FIELD_SIZE);
		}
		return energyErrorSpinner;
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

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		parameters.setEnergyError(((Number) getEnergyErrorSpinner().getValue()).floatValue());

	}

	public void validatePanel(ValidationErrors errors) {

		// nothing to do

	}

}
