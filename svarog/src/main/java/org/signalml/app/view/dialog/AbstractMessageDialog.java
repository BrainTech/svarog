/* AbstractMessageDialog.java created 2008-03-03
 *
 */

package org.signalml.app.view.dialog;

import static org.signalml.app.SvarogApplication._;
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
import org.signalml.plugin.export.SignalMLException;

/**
 * Abstract dialog, which displays the message to the user.
 * Contains the {@link #getMessageLabel() label} with the text of the message
 * and the {@link #getDontShowAgainCheckBox() check-box} which allows the user
 * to select if the dialog for this message should be shown again.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractMessageDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * the check-box which tells that this message (the dialog with the same
	 * message as this dialog) shouldn't be shown again
	 */
	private JCheckBox dontShowAgainCheckBox;
	
	/**
	 * the panel with {@link #messageLabel}
	 */
	private JPanel messagePanel;
	/**
	 * the label in which the text of the message is shown
	 */
	private JLabel messageLabel;

	/**
	 * the {@link ApplicationConfiguration configuration} of Svarog
	 */
	private ApplicationConfiguration applicationConfig;
	
	/**
	 * the Preferences of Svarog
	 */
	private Preferences preferences;

	/**
	 * Constructor. Sets the source of messages.
	 */
	public AbstractMessageDialog() {
		super();
	}

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public AbstractMessageDialog(Window w, boolean isModal) {
		super( w, isModal);
	}

	/**
	 * Calls the {@link AbstractDialog#initialize() initialization} in parent
	 * and sets this dialog to be not resizable.
	 */
	@Override
	protected void initialize() {
		super.initialize();
		setResizable(false);
	}

	/**
	 * Calls the {@link AbstractDialog#createControlPane() creation} of the
	 * control pane in parent and adds {@link #getDontShowAgainCheckBox()
	 * check-box} to it.
	 */
	@Override
	protected JPanel createControlPane() {
		JPanel controlPane = super.createControlPane();
		controlPane.add(Box.createHorizontalStrut(10), 1);
		controlPane.add(getDontShowAgainCheckBox(), 0);
		return controlPane;
	}

	/**
	 * Creates the interface with only the {@link #getMessagePanel() message
	 * panel}.
	 */
	@Override
	public JComponent createInterface() {
		return getMessagePanel();
	}

	/**
	 * Returns the check-box which tells that this message (the dialog with
	 * the same message as this dialog) shouldn't be shown again.
	 * If the check-box doesn't exist it is created.
	 * @return the check-box which tells that this message (the dialog with
	 * the same message as this dialog) shouldn't be shown again
	 */
	public JCheckBox getDontShowAgainCheckBox() {
		if (dontShowAgainCheckBox == null) {
			dontShowAgainCheckBox = new JCheckBox(_("Don't show this message again"));
			dontShowAgainCheckBox.setFont(dontShowAgainCheckBox.getFont().deriveFont(Font.PLAIN,10F));
		}
		return dontShowAgainCheckBox;
	}

	/**
	 * Returns the panel with the {@link #getMessageLabel() label} with the
	 * message of this dialog.
	 * If the panel doesn't exist it is created 
	 * @return the panel with the label
	 */
	public JPanel getMessagePanel() {
		if (messagePanel == null) {
			messagePanel = new JPanel(new BorderLayout());
			messagePanel.add(getMessageLabel(), BorderLayout.CENTER);
		}
		return messagePanel;
	}

	/**
	 * Returns the label with the message for this dialog.
	 * If the label doesn't exist it is created.
	 * <p>NOTE: the text of the message must be set in implementation.
	 * @return the label with the message for this dialog.
	 */
	public JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel();
			messageLabel.setFont(messageLabel.getFont().deriveFont(Font.PLAIN, 12));
		}
		return messageLabel;
	}

	/**
	 * Returns if the dialog with the same message as this dialog
	 * should be shown again.
	 * @return {@code true} if the dialog shouldn't be shown again,
	 * {@code false} otherwise
	 */
	public abstract boolean getDontShowAgain();

	/**
	 * Sets if the dialog with the same message as this dialog
	 * should be shown again.
	 * @param dontShow {@code true} if the dialog shouldn't be shown again,
	 * {@code false} otherwise
	 */
	public abstract void setDontShowAgain(boolean dontShow);

	/**
	 * Sets the stage of {@link #getDontShowAgainCheckBox() dontShow ckeck-box}.
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		getDontShowAgainCheckBox().setSelected(getDontShowAgain());

	}

	/**
	 * {@link #setDontShowAgain(boolean) Sets} if this dialog should
	 * be shown again (saves users decision).
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		setDontShowAgain(getDontShowAgainCheckBox().isSelected());

	}

	/**
	 * There is no model for this dialog, so the class should be null.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null);
	}

	/**
	 * Returns the {@link ApplicationConfiguration configuration} of Svarog.
	 * @return the configuration of Svarog
	 */
	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	/**
	 * Sets the {@link ApplicationConfiguration configuration} of Svarog.
	 * @param applicationConfig the configuration of Svarog
	 */
	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	/**
	 * Returns the preferences of Svarog.
	 * @return the preferences of Svarog
	 */
	public Preferences getPreferences() {
		return preferences;
	}

	/**
	 * Sets the preferences of Svarog.
	 * @param preferences the preferences of Svarog
	 */
	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

}
