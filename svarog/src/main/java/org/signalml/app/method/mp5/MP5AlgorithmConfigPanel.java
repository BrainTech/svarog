/* MP5AlgorithmConfigPanel.java created 2008-01-30
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
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.common.components.CompactButton;
import org.signalml.app.view.common.components.ResolvableComboBox;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.method.mp5.MP5Algorithm;
import org.signalml.method.mp5.MP5Parameters;

import org.springframework.validation.Errors;

/** MP5AlgorithmConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5AlgorithmConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AbstractDialog owner;

	private ResolvableComboBox algorithmComboBox;

	private MP5Algorithm lastAlgorithm;

	public MP5AlgorithmConfigPanel(AbstractDialog owner) {
		super();
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Selection of the algorithm")),
			new EmptyBorder(3,3,3,3)
		);

		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel algorithmLabel = new JLabel(_("MP version"));

//		CompactButton algorithmHelpButton = SwingUtils.createFieldHelpButton(owner, MP5MethodDialog.HELP_ALGORITHM);

		Component glue1 = Box.createHorizontalGlue();

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(algorithmLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(glue1)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getAlgorithmComboBox())
		);

//		hGroup.addGroup(
//			layout.createParallelGroup()
//			.addComponent(algorithmHelpButton)
//		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.CENTER)
			.addComponent(algorithmLabel)
			.addComponent(glue1)
			.addComponent(getAlgorithmComboBox())
//			.addComponent(algorithmHelpButton)
		);

		layout.setVerticalGroup(vGroup);

	}

	public ResolvableComboBox getAlgorithmComboBox() {
		if (algorithmComboBox == null) {
			algorithmComboBox = new ResolvableComboBox();
			algorithmComboBox.setModel(new DefaultComboBoxModel(MP5Algorithm.values()));
			algorithmComboBox.setPreferredSize(MP5MethodDialog.FIELD_SIZE);
			algorithmComboBox.setMaximumSize(MP5MethodDialog.FIELD_SIZE);
			algorithmComboBox.setMinimumSize(MP5MethodDialog.FIELD_SIZE);
		}
		return algorithmComboBox;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {

		if (getAlgorithmComboBox().isEnabled())
			getAlgorithmComboBox().setSelectedItem(parameters.getAlgorithm());
		lastAlgorithm = null;

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		parameters.setAlgorithm((MP5Algorithm) getAlgorithmComboBox().getSelectedItem());

	}

	public void validatePanel(ValidationErrors errors) {

		// nothing to do

	}

	public void setMMPEnabled(boolean mmpEnabled) {
		if (mmpEnabled != algorithmComboBox.isEnabled()) {
			ResolvableComboBox algorithmBox = getAlgorithmComboBox();
			if (mmpEnabled) {
				if (lastAlgorithm != null) {
					algorithmBox.setSelectedItem(lastAlgorithm);
				}
			} else {
				lastAlgorithm = (MP5Algorithm) algorithmBox.getSelectedItem();
				algorithmBox.setSelectedItem(MP5Algorithm.SMP);
			}

			algorithmBox.setEnabled(mmpEnabled);
			algorithmBox.repaint();
		}
	}

}
