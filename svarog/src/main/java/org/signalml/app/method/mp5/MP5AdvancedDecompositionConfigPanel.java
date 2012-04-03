/* MP5AdvancedDecompositionConfigPanel.java created 2008-01-31
 *
 */
package org.signalml.app.method.mp5;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
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
import org.signalml.app.view.components.ResolvableComboBox;
import org.signalml.app.view.components.dialogs.AbstractDialog;
import org.signalml.method.mp5.MP5DictionaryReinitType;
import org.signalml.method.mp5.MP5DictionaryType;
import org.signalml.method.mp5.MP5Parameters;

/** MP5AdvancedDecompositionConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5AdvancedDecompositionConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AbstractDialog owner;

	private ResolvableComboBox dictionaryTypeComboBox;
	private ResolvableComboBox dictionaryReinitTypeComboBox;
	private JSpinner scaleToPeriodFactorSpinner;

	public MP5AdvancedDecompositionConfigPanel(AbstractDialog owner) {
		super();
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(_("Advanced decomposition settings")),
		        new EmptyBorder(3,3,3,3)
		);

		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel dictionaryTypeLabel = new JLabel(_("Dictionary type"));
		JLabel dictionaryReinitTypeLabel = new JLabel(_("Reinit dictionary"));
		JLabel scaleToPeriodFactorLabel = new JLabel(_("Max scale / period"));

		CompactButton dictionaryTypeHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_DICTIONARY_TYPE);
		CompactButton dictionaryReinitTypeHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_DICTIONARY_REINIT_TYPE);
		CompactButton scaleToPeriodFactorHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_SCALE_TO_PERIOD_FACTOR);

		Component glue1 = Box.createHorizontalGlue();
		Component glue2 = Box.createHorizontalGlue();
		Component glue3 = Box.createHorizontalGlue();

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(dictionaryTypeLabel)
		        .addComponent(dictionaryReinitTypeLabel)
		        .addComponent(scaleToPeriodFactorLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(glue1)
		        .addComponent(glue2)
		        .addComponent(glue3)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(getDictionaryTypeComboBox())
		        .addComponent(getDictionaryReinitTypeComboBox())
		        .addComponent(getScaleToPeriodFactorSpinner())
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(dictionaryTypeHelpButton)
		        .addComponent(dictionaryReinitTypeHelpButton)
		        .addComponent(scaleToPeriodFactorHelpButton)
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(dictionaryTypeLabel)
				.addComponent(glue1)
				.addComponent(getDictionaryTypeComboBox())
				.addComponent(dictionaryTypeHelpButton)
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(dictionaryReinitTypeLabel)
				.addComponent(glue2)
				.addComponent(getDictionaryReinitTypeComboBox())
				.addComponent(dictionaryReinitTypeHelpButton)
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(scaleToPeriodFactorLabel)
				.addComponent(glue3)
				.addComponent(getScaleToPeriodFactorSpinner())
				.addComponent(scaleToPeriodFactorHelpButton)
			);

		layout.setVerticalGroup(vGroup);				
								
	}

	public ResolvableComboBox getDictionaryTypeComboBox() {
		if (dictionaryTypeComboBox == null) {
			dictionaryTypeComboBox = new ResolvableComboBox();
			dictionaryTypeComboBox.setModel(new DefaultComboBoxModel(MP5DictionaryType.values()));
			dictionaryTypeComboBox.setPreferredSize(MP5MethodDialog.FIELD_SIZE);
			dictionaryTypeComboBox.setMinimumSize(MP5MethodDialog.FIELD_SIZE);
			dictionaryTypeComboBox.setMaximumSize(MP5MethodDialog.FIELD_SIZE);
		}
		return dictionaryTypeComboBox;
	}

	public ResolvableComboBox getDictionaryReinitTypeComboBox() {
		if (dictionaryReinitTypeComboBox == null) {
			dictionaryReinitTypeComboBox = new ResolvableComboBox();
			dictionaryReinitTypeComboBox.setModel(new DefaultComboBoxModel(MP5DictionaryReinitType.values()));
			dictionaryReinitTypeComboBox.setPreferredSize(MP5MethodDialog.FIELD_SIZE);
			dictionaryReinitTypeComboBox.setMinimumSize(MP5MethodDialog.FIELD_SIZE);
			dictionaryReinitTypeComboBox.setMaximumSize(MP5MethodDialog.FIELD_SIZE);
		}
		return dictionaryReinitTypeComboBox;
	}

	@SuppressWarnings("cast")
	public JSpinner getScaleToPeriodFactorSpinner() {
		if (scaleToPeriodFactorSpinner == null) {
			scaleToPeriodFactorSpinner = new JSpinner(
			        new SpinnerNumberModel(
			                ((double) MP5Parameters.MIN_SCALE_TO_PERIOD_FACTOR),
			                ((double) MP5Parameters.MIN_SCALE_TO_PERIOD_FACTOR),
			                ((double) MP5Parameters.MAX_SCALE_TO_PERIOD_FACTOR),
			                0.1d
			        )
			);
			scaleToPeriodFactorSpinner.setPreferredSize(MP5MethodDialog.FIELD_SIZE);
			scaleToPeriodFactorSpinner.setMinimumSize(MP5MethodDialog.FIELD_SIZE);
			scaleToPeriodFactorSpinner.setMaximumSize(MP5MethodDialog.FIELD_SIZE);
		}
		return scaleToPeriodFactorSpinner;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {

		getDictionaryTypeComboBox().setSelectedItem(parameters.getDictionaryType());
		getDictionaryReinitTypeComboBox().setSelectedItem(parameters.getDictionaryReinitType());
		getScaleToPeriodFactorSpinner().setValue(new Double(parameters.getScaleToPeriodFactor()));

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		parameters.setDictionaryType((MP5DictionaryType) getDictionaryTypeComboBox().getSelectedItem());
		parameters.setDictionaryReinitType((MP5DictionaryReinitType) getDictionaryReinitTypeComboBox().getSelectedItem());
		parameters.setScaleToPeriodFactor(((Number) getScaleToPeriodFactorSpinner().getValue()).floatValue());

	}

	public void validatePanel(ValidationErrors errors) {

		// nothing to do

	}

}
