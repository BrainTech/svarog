/* SeriousWarningDialog.java created 2007-11-22
 *
 */

package org.signalml.app.view.dialog;

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
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/** SeriousWarningDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SeriousWarningDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private ApplicationConfiguration applicationConfig;

	private JLabel messageLabel;
	private int currentTimeout;

	private Timer timeoutTimer;
	private ActionListener timeoutListener;

	public SeriousWarningDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public SeriousWarningDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("seriousWarning.title"));
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

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());
		interfacePanel.setBorder(new EmptyBorder(3,3,3,3));

		JLabel bombLabel = new JLabel(IconUtils.loadClassPathIcon("org/signalml/app/icon/bomblarge.png"));

		JLabel warningLabel = new JLabel(messageSource.getMessage("seriousWarning.warning"));

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

	public JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel();
			messageLabel.setFont(messageLabel.getFont().deriveFont(Font.PLAIN, 12F));
			setMessageText("&nbsp");
		}
		return messageLabel;
	}

	private void updateOkAction() {
		if (currentTimeout > 0) {
			getOkAction().putValue(AbstractAction.NAME, messageSource.getMessage("seriousWarning.doItTimeout", new Object[] { currentTimeout }));
			getOkAction().setEnabled(false);
			timeoutTimer.restart();
		} else {
			getOkAction().putValue(AbstractAction.NAME, messageSource.getMessage("seriousWarning.doIt"));
			getOkAction().setEnabled(true);
		}
		getOkButton().paintImmediately(new Rectangle(new Point(0,0), getOkButton().getSize()));
	}

	private void setMessageText(String text) {
		getMessageLabel().setText("<html><body><div style=\"width: 300px;\">" + text + "</div></body></html>");
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		SeriousWarningDescriptor descriptor = (SeriousWarningDescriptor) model;
		currentTimeout = descriptor.getTimeout();

		updateOkAction();

		setMessageText(descriptor.getWarning());

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// do nothing
	}

	@Override
	public boolean showDialog(Object model) {

		// check if bombs have been disabled for easier testing
		if (applicationConfig.isDisableSeriousWarnings()) {
			return true;
		}

		return super.showDialog(model);

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SeriousWarningDescriptor.class.isAssignableFrom(clazz);
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

}
