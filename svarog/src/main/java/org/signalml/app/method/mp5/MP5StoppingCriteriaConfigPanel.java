/* MP5StoppingCriteriaConfigPanel.java created 2008-01-30
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
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.components.CompactButton;
import org.signalml.app.view.components.dialogs.AbstractDialog;
import org.signalml.method.mp5.MP5Parameters;

import org.springframework.validation.Errors;

/** MP5StoppingCriteriaConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5StoppingCriteriaConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AbstractDialog owner;

	private JSpinner maxIterationCountSpinner;
	private JSpinner energyPercentSpinner;

	public MP5StoppingCriteriaConfigPanel(AbstractDialog owner) {
		super();
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(_("Stopping criteria")),
		        new EmptyBorder(3,3,3,3)
		);

		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel maxIterationCountLabel = new JLabel(_("Max iterations"));
		JLabel energyPercentLabel = new JLabel(_("Energy percent"));

		Component glue1 = Box.createHorizontalGlue();
		Component glue2 = Box.createHorizontalGlue();

		CompactButton maxIterationCountHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_MAX_ITERATION_COUNT);
		CompactButton energyPercentHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_ENERGY_PERCENT);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(maxIterationCountLabel)
		        .addComponent(energyPercentLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(glue1)
		        .addComponent(glue2)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(getMaxIterationCountSpinner())
		        .addComponent(getEnergyPercentSpinner())
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(maxIterationCountHelpButton)
		        .addComponent(energyPercentHelpButton)
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(maxIterationCountLabel)
				.addComponent(glue1)
				.addComponent(getMaxIterationCountSpinner())
				.addComponent(maxIterationCountHelpButton)
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(energyPercentLabel)
				.addComponent(glue2)
				.addComponent(getEnergyPercentSpinner())
				.addComponent(energyPercentHelpButton)
			);

		layout.setVerticalGroup(vGroup);

	}

	public JSpinner getMaxIterationCountSpinner() {
		if (maxIterationCountSpinner == null) {
			maxIterationCountSpinner = new JSpinner(
			        new SpinnerNumberModel(
			                MP5Parameters.MIN_ITERATION_COUNT,
			                MP5Parameters.MIN_ITERATION_COUNT,
			                MP5Parameters.MAX_ITERATION_COUNT,
			                1
			        )
			);
			maxIterationCountSpinner.setPreferredSize(MP5MethodDialog.FIELD_SIZE);
			maxIterationCountSpinner.setMaximumSize(MP5MethodDialog.FIELD_SIZE);
			maxIterationCountSpinner.setMinimumSize(MP5MethodDialog.FIELD_SIZE);
		}
		return maxIterationCountSpinner;
	}

	@SuppressWarnings("cast")
	public JSpinner getEnergyPercentSpinner() {
		if (energyPercentSpinner == null) {
			energyPercentSpinner = new JSpinner(
			        new SpinnerNumberModel(
			                ((double) MP5Parameters.MIN_ENERGY_PERCENT),
			                ((double) MP5Parameters.MIN_ENERGY_PERCENT),
			                ((double) MP5Parameters.MAX_ENERGY_PERCENT),
			                0.1d
			        )
			);
			energyPercentSpinner.setPreferredSize(MP5MethodDialog.FIELD_SIZE);
			energyPercentSpinner.setMaximumSize(MP5MethodDialog.FIELD_SIZE);
			energyPercentSpinner.setMinimumSize(MP5MethodDialog.FIELD_SIZE);
		}
		return energyPercentSpinner;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {

		getMaxIterationCountSpinner().setValue(parameters.getMaxIterationCount());
		getEnergyPercentSpinner().setValue(new Double(parameters.getEnergyPercent()));

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		parameters.setMaxIterationCount(((Number) getMaxIterationCountSpinner().getValue()).intValue());
		parameters.setEnergyPercent(((Number) getEnergyPercentSpinner().getValue()).floatValue());

	}

	public void validatePanel(ValidationErrors errors) {

		// nothing to do

	}

}
