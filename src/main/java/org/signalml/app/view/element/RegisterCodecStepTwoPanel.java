/* RegisterCodecStepTwoPanel.java created 2007-09-18
 *
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.signalml.app.util.IconUtils;
import org.springframework.context.support.MessageSourceAccessor;

/** RegisterCodecStepTwoPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RegisterCodecStepTwoPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private JPanel formatNamePanel = null;
	private JTextField nameField = null;
	private JLabel warningLabel = null;

	/**
	 * This is the default constructor
	 */
	public RegisterCodecStepTwoPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		add(getFormatNamePanel(), BorderLayout.CENTER);

	}

	private JPanel getFormatNamePanel() {
		if (formatNamePanel == null) {
			formatNamePanel = new JPanel();
			formatNamePanel.setBorder(BorderFactory.createTitledBorder(messageSource.getMessage("registerCodec.chooseFormatName")));
			formatNamePanel.setLayout(new BorderLayout());
			TitledComponentPanel namePanel = new TitledComponentPanel(messageSource.getMessage("registerCodec.formatName"),getNameField());
			namePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			formatNamePanel.add(namePanel,BorderLayout.NORTH);
			formatNamePanel.add(getWarningLabel(),BorderLayout.CENTER);
		}

		return formatNamePanel;
	}

	public JTextField getNameField() {
		if (nameField == null) {
			nameField = new JTextField();
			nameField.setPreferredSize(new Dimension(200,25));
		}
		return nameField;
	}

	public JLabel getWarningLabel() {
		if (warningLabel == null) {
			warningLabel = new JLabel(messageSource.getMessage("registerCodec.nameExistsWarning"));
			warningLabel.setIcon(IconUtils.getWarningIcon());
			warningLabel.setHorizontalAlignment(JLabel.CENTER);
			warningLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return warningLabel;
	}

}
