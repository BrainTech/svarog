/* SeriousWarningDialog.java created 2007-11-22
 *
 */

package org.signalml.app.view.dialog;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.SeriousWarningDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.SignalMLException;

/**
 * The dialog which displays the warnings that were considered 'serious'.
 * Contains the label with the large bomb to attract users attention.
 * Moreover the OK button is activated after the specified timeout passes. 
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SeriousWarningDialog extends org.signalml.app.view.dialog.AbstractSvarogDialog  {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link ApplicationConfiguration configuration} of Svarog
	 */
	private ApplicationConfiguration applicationConfig;

	/**
	 * the label with the warning messagge
	 */
	private JLabel messageLabel;
	/**
	 * the remaining timeout until the accept button will be clickable
	 */
	private int currentTimeout;

	/**
	 * the timer which counts the timeout until the accept button will be
	 * clickable
	 */
	private Timer timeoutTimer;
	/**
	 * the listener for {@link #timeoutTimer}
	 */
	private ActionListener timeoutListener;

	/**
	 * Constructor. Sets the source of messages.
	 * @param messageSource the source of messages
	 */
	public  SeriousWarningDialog() {
		super();
	}

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param messageSource message source to set
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public  SeriousWarningDialog( Window w, boolean isModal) {
		super( w, isModal);
	}

	/**
	 * Initializes this dialog:
	 * <uL>
	 * <li>sets the icon and the title,</li>
	 * <li>creates the timeout listener, which decreases the timeout left and
	 * sets it in the timer,</li>
	 * <li>adds a window listener, which stops the timer if the window is
	 * closing.</li></ul>
	 */
	@Override
	protected void initialize() {
		setTitle(_("WARNING! Are you sure?"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/bomb.png"));
		super.initialize();

		timeoutListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentTimeout > 0) {
					currentTimeout--;
				}
				updateOkAction();
				if (currentTimeout <= 0) {
					timeoutTimer.stop();
				}
			}

		};

		timeoutTimer = new Timer(1000, timeoutListener);
		timeoutTimer.setRepeats(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (timeoutTimer.isRunning()) {
					timeoutTimer.stop();
				}
			}
		});

	}

	/**
	 * Creates the interface for this dialog which consists of:
	 * <ul>
	 * <li>the label with the bomb,</li>
	 * <li>the panel with the warning.</li>
	 * </ul>
	 */
	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());
		interfacePanel.setBorder(new EmptyBorder(3,3,3,3));

		JLabel bombLabel = new JLabel(IconUtils.loadClassPathIcon("org/signalml/app/icon/bomblarge.png"));

		JLabel warningLabel = new JLabel(_("WARNING! You are about to perform an irreversible operation."));

		JPanel labelPanel = new JPanel();
		labelPanel.setBorder(new EmptyBorder(0,10,0,0));
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

		labelPanel.add(warningLabel);
		labelPanel.add(Box.createVerticalStrut(10));
		labelPanel.add(getMessageLabel());

		interfacePanel.add(bombLabel, BorderLayout.WEST);
		interfacePanel.add(labelPanel, BorderLayout.CENTER);

		return interfacePanel;

	}

	/**
	 * Returns the label with the warning message.
	 * If the label doesn't exist, it is created with empty text.
	 * @return the label with the warning message.
	 */
	public JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel();
			messageLabel.setFont(messageLabel.getFont().deriveFont(Font.PLAIN, 12F));
			setMessageText("&nbsp");
		}
		return messageLabel;
	}

	/**
	 * Updates the state of OK button.
	 * If the remaining timeout is greater then 0 makes the button inactive and
	 * displays the information about the remaining timeout.
	 * Otherwise enables the OK button.
	 */
	private void updateOkAction() {
		if (currentTimeout > 0) {
			getOkAction().putValue(AbstractAction.NAME, java.text.MessageFormat.format(_("Please reconsider... ({0})"), new Object[] { currentTimeout }));
			getOkAction().setEnabled(false);
			timeoutTimer.restart();
		} else {
			getOkAction().putValue(AbstractAction.NAME, _("I am completely sure! Do it!"));
			getOkAction().setEnabled(true);
		}
		getOkButton().paintImmediately(new Rectangle(new Point(0,0), getOkButton().getSize()));
	}

	/**
	 * Sets the text of the warning.
	 * @param text the text of the warning
	 */
	private void setMessageText(String text) {
		getMessageLabel().setText("<html><body><div style=\"width: 300px;\">" + text + "</div></body></html>");
	}

	/**
	 * Fills the fields of this dialog using the given {@link
	 * SeriousWarningDescriptor model}:
	 * <ul><li>sets the timeout and updates OK button,</li>
	 * <li>set the text of the warning message.</li></ul>
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		SeriousWarningDescriptor descriptor = (SeriousWarningDescriptor) model;
		currentTimeout = descriptor.getTimeout();

		updateOkAction();

		setMessageText(descriptor.getWarning());

	}

	/**
	 * Does nothing.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// do nothing
	}

	/**
	 * If the serious warnings were disabled returns, otherwise shows this
	 * dialog.
	 */
	@Override
	public boolean showDialog(Object model) {

		// check if bombs have been disabled for easier testing
		if (applicationConfig.isDisableSeriousWarnings()) {
			return true;
		}

		return super.showDialog(model);

	}

	/**
	 * The model for this dialog must be of type {@link SeriousWarningDescriptor}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SeriousWarningDescriptor.class.isAssignableFrom(clazz);
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

}
