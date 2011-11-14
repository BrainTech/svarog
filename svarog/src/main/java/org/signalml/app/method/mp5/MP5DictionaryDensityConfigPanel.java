/* MP5DictionaryDensityConfigPanel.java created 2008-01-30
 *
 */
package org.signalml.app.method.mp5;

import static org.signalml.app.SvarogApplication._;
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

import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.app.view.element.CompactButton;
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

	private JSpinner dilationFactorSpinner;
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

		JLabel dilationFactorLabel = new JLabel(_("Dilation factor \"a\""));
		JLabel atomCountLabel = new JLabel(_("Atoms in dictionary"));
		JLabel ramUsageLabel = new JLabel(_("Approximate RAM usage"));

		Component glue1 = Box.createHorizontalGlue();
		Component glue2 = Box.createHorizontalGlue();
		Component glue3 = Box.createHorizontalGlue();

		CompactButton dilationFactorHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_DILATION_FACTOR);
		CompactButton atomCountHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_ATOM_COUNT);
		CompactButton ramUsageHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_RAM_USAGE);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(dilationFactorLabel)
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
		        .addComponent(getDilationFactorSpinner())
		        .addComponent(getAtomCountTextField())
		        .addComponent(getRamUsageTextField())
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(dilationFactorHelpButton)
		        .addComponent(atomCountHelpButton)
		        .addComponent(ramUsageHelpButton)
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(dilationFactorLabel)
				.addComponent(glue1)
				.addComponent(getDilationFactorSpinner())
				.addComponent(dilationFactorHelpButton)
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(atomCountLabel)
				.addComponent(glue2)
				.addComponent(getAtomCountTextField())
				.addComponent(atomCountHelpButton)
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(ramUsageLabel)
				.addComponent(glue3)
				.addComponent(getRamUsageTextField())
				.addComponent(ramUsageHelpButton)
			);
		
		layout.setVerticalGroup(vGroup);				
						
	}

	@SuppressWarnings("cast")
	public JSpinner getDilationFactorSpinner() {
		if (dilationFactorSpinner == null) {
			dilationFactorSpinner = new JSpinner(
			        new SpinnerNumberModel(
			                ((double) MP5Parameters.MIN_DILATION_FACTOR),
			                ((double) MP5Parameters.MIN_DILATION_FACTOR),
			                ((double) MP5Parameters.MAX_DILATION_FACTOR),
			                0.1d
			        )
			);
			dilationFactorSpinner.setPreferredSize(MP5MethodDialog.FIELD_SIZE);
			dilationFactorSpinner.setMaximumSize(MP5MethodDialog.FIELD_SIZE);
			dilationFactorSpinner.setMinimumSize(MP5MethodDialog.FIELD_SIZE);
		}
		return dilationFactorSpinner;
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

		getDilationFactorSpinner().setValue(new Double(parameters.getDilationFactor()));

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		parameters.setDilationFactor(((Number) getDilationFactorSpinner().getValue()).floatValue());

	}

	public void validatePanel(Errors errors) {

		// nothing to do

	}

}
