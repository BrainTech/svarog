/* StagerEnableAdvancedConfigPanel.java created 2008-02-14
 * 
 */
package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.newstager.NewStagerPlugin._;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.plugin.newstager.data.NewStagerParameters;

/**
 * StagerEnableAdvancedConfigPanel
 * 
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerEnableAdvancedConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JCheckBox enableAdvancedCheckBox;

	//TODO!
	//private EnableAction invertedEnableAction;

	public NewStagerEnableAdvancedConfigPanel(/*EnableAction invertedEnableAction*/) {
		super();
		//this.invertedEnableAction = invertedEnableAction;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout(3, 3));

		CompoundBorder border = new CompoundBorder(new TitledBorder(
				_("Advanced config")), new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		JLabel enableAdvancedLabel = new JLabel(_("Enable advanced config"));

		add(enableAdvancedLabel, BorderLayout.CENTER);
		add(getEnableAdvancedCheckBox(), BorderLayout.EAST);

	}

	public JCheckBox getEnableAdvancedCheckBox() {
		if (enableAdvancedCheckBox == null) {
			enableAdvancedCheckBox = new JCheckBox();
			enableAdvancedCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//TODO!
					/*if (invertedEnableAction != null) {
						invertedEnableAction.setEnabled(!enableAdvancedCheckBox
								.isSelected());
					}*/
				}
			});
		}
		return enableAdvancedCheckBox;
	}

	public void fillPanelFromParameters(NewStagerParameters parameters) {
		//TODO!
		//getEnableAdvancedCheckBox().setSelected(parameters.isAdvancedConfig());
	}

	public void fillParametersFromPanel(NewStagerParameters parameters) {
		//TODO!
		//parameters.setAdvancedConfig(getEnableAdvancedCheckBox().isSelected());
	}

	public void validatePanel(ValidationErrors errors) {
		// nothing to do
	}

}
