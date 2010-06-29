/* StagerEnableAdvancedConfigPanel.java created 2008-02-14
 *
 */
package org.signalml.app.method.stager;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.action.EnableAction;
import org.signalml.method.stager.StagerParameters;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** StagerEnableAdvancedConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerEnableAdvancedConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private JCheckBox enableAdvancedCheckBox;

	private EnableAction invertedEnableAction;

	public StagerEnableAdvancedConfigPanel(MessageSourceAccessor messageSource, EnableAction invertedEnableAction) {
		super();
		this.invertedEnableAction = invertedEnableAction;
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout(3,3));

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("stagerMethod.dialog.enableAdvancedConfigTitle")),
		        new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		JLabel enableAdvancedLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.enableAdvancedConfig"));

		add(enableAdvancedLabel, BorderLayout.CENTER);
		add(getEnableAdvancedCheckBox(), BorderLayout.EAST);

	}

	public JCheckBox getEnableAdvancedCheckBox() {
		if (enableAdvancedCheckBox == null) {
			enableAdvancedCheckBox = new JCheckBox();
			enableAdvancedCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (invertedEnableAction != null) {
						invertedEnableAction.setEnabled(!enableAdvancedCheckBox.isSelected());
					}
				}
			});
		}
		return enableAdvancedCheckBox;
	}

	public void fillPanelFromParameters(StagerParameters parameters) {

		getEnableAdvancedCheckBox().setSelected(parameters.isAdvancedConfig());

	}

	public void fillParametersFromPanel(StagerParameters parameters) {

		parameters.setAdvancedConfig(getEnableAdvancedCheckBox().isSelected());

	}

	public void validatePanel(Errors errors) {

		// nothing to do

	}

}
