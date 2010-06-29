/* AbstractMessageDialog.java created 2008-03-03
 *
 */

package org.signalml.app.view.dialog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/** AbstractMessageDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractMessageDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private JCheckBox dontShowAgainCheckBox;
	private JPanel messagePanel;
	private JLabel messageLabel;

	private ApplicationConfiguration applicationConfig;
	private Preferences preferences;

	public AbstractMessageDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public AbstractMessageDialog(MessageSourceAccessor messageSource,Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		super.initialize();
		setResizable(false);
	}

	@Override
	protected JPanel createControlPane() {
		JPanel controlPane = super.createControlPane();
		controlPane.add(Box.createHorizontalStrut(10), 1);
		controlPane.add(getDontShowAgainCheckBox(), 0);
		return controlPane;
	}

	@Override
	public JComponent createInterface() {
		return getMessagePanel();
	}

	public JCheckBox getDontShowAgainCheckBox() {
		if (dontShowAgainCheckBox == null) {
			dontShowAgainCheckBox = new JCheckBox(messageSource.getMessage("messageDialog.dontShowAgain"));
			dontShowAgainCheckBox.setFont(dontShowAgainCheckBox.getFont().deriveFont(Font.PLAIN,10F));
		}
		return dontShowAgainCheckBox;
	}

	public JPanel getMessagePanel() {
		if (messagePanel == null) {
			messagePanel = new JPanel(new BorderLayout());
			messagePanel.add(getMessageLabel(), BorderLayout.CENTER);
		}
		return messagePanel;
	}

	public JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel();
			messageLabel.setFont(messageLabel.getFont().deriveFont(Font.PLAIN, 12));
		}
		return messageLabel;
	}

	public abstract boolean getDontShowAgain();

	public abstract void setDontShowAgain(boolean dontShow);

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		getDontShowAgainCheckBox().setSelected(getDontShowAgain());

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		setDontShowAgain(getDontShowAgainCheckBox().isSelected());

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null);
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

}
