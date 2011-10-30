/* MP5AlgorithmConfigPanel.java created 2008-01-30
 *
 */
package org.signalml.app.method.mp5;

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

import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.app.view.element.CompactButton;
import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.method.mp5.MP5Algorithm;
import org.signalml.method.mp5.MP5Parameters;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** MP5AlgorithmConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5AlgorithmConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private AbstractDialog owner;

	private ResolvableComboBox algorithmComboBox;

	private MP5Algorithm lastAlgorithm;

	public MP5AlgorithmConfigPanel(MessageSourceAccessor messageSource, AbstractDialog owner) {
		super();
		this.messageSource = messageSource;
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("mp5Method.dialog.algorithmTitle")),
		        new EmptyBorder(3,3,3,3)
		);

		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel algorithmLabel = new JLabel(messageSource.getMessage("mp5Method.dialog.algorithm"));

		CompactButton algorithmHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, MP5MethodDialog.HELP_ALGORITHM);

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

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(algorithmHelpButton)
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(algorithmLabel)
				.addComponent(glue1)
				.addComponent(getAlgorithmComboBox())
				.addComponent(algorithmHelpButton)
			);
					
		layout.setVerticalGroup(vGroup);				
						
	}

	public ResolvableComboBox getAlgorithmComboBox() {
		if (algorithmComboBox == null) {
			algorithmComboBox = new ResolvableComboBox(messageSource);
			algorithmComboBox.setModel(new DefaultComboBoxModel(MP5Algorithm.values()));
			algorithmComboBox.setPreferredSize(MP5MethodDialog.FIELD_SIZE);
			algorithmComboBox.setMaximumSize(MP5MethodDialog.FIELD_SIZE);
			algorithmComboBox.setMinimumSize(MP5MethodDialog.FIELD_SIZE);
		}
		return algorithmComboBox;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {

		getAlgorithmComboBox().setSelectedItem(parameters.getAlgorithm());
		lastAlgorithm = null;

	}

	public void fillParametersFromPanel(MP5Parameters parameters) {

		parameters.setAlgorithm((MP5Algorithm) getAlgorithmComboBox().getSelectedItem());

	}

	public void validatePanel(Errors errors) {

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
