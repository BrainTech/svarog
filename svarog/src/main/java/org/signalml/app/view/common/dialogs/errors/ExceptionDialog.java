/* ErrorsDialog.java created 2007-09-19
 *
 */

package org.signalml.app.view.common.dialogs.errors;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.common.dialogs.AbstractMessageDialog;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.Errors;

/**
 * Dialog for showing an exception.
 *
 * @author Piotr Szachewicz
 */
public class ExceptionDialog extends AbstractMessageDialog  {

	private Icon icon;
	private JTextArea stacktraceTextArea;

	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public ExceptionDialog(Window w) {
		super(w, true);
		setTitle(_("Exception occurred!"));
		setMinimumSize(new Dimension(470, 250));
		setLocationRelativeTo(null);

		icon = IconUtils.getErrorIcon();

	}

	@Override
	protected void initialize() {
		super.initialize();

		setResizable(true);
	}

	/**
	 * The model for this dialog must be either of type {@link Errors} or
	 * {@link MessageSourceResolvable}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return Throwable.class.isAssignableFrom(clazz);
	}

	@Override
	public JPanel getMessagePanel() {
		if (messagePanel == null) {
			messagePanel = new JPanel(new BorderLayout());
			messagePanel.add(getMessageLabel(), BorderLayout.NORTH);
			getMessageLabel().setIcon(icon);

			JScrollPane scrollPane = new JScrollPane(getStacktraceTextArea());
		    messagePanel.add(scrollPane, BorderLayout.CENTER);
		}
		return messagePanel;
	}

	public JTextArea getStacktraceTextArea() {
		if (stacktraceTextArea == null) {
			stacktraceTextArea = new JTextArea(4, 10);
			stacktraceTextArea.setEditable(false);
		}
		return stacktraceTextArea;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		Throwable throwable = (Throwable) model;

		getMessageLabel().setText(getErrorMessage(throwable));
		getStacktraceTextArea().setText(getStackTraceReport(throwable));
		getStacktraceTextArea().setCaretPosition(0);
	}

	protected String getErrorMessage(Throwable throwable) {
		StringBuilder sb = new StringBuilder();

		sb.append(_("An unexpected error occurred"));
		sb.append(" (");
		sb.append(throwable.getClass().getSimpleName());
		sb.append(").");

		return sb.toString();
	}

	protected String getStackTraceReport(Throwable throwable) {
		StringBuilder sb = new StringBuilder();

		if (throwable.getLocalizedMessage() != null) {
			sb.append("message: " + throwable.getLocalizedMessage());
		}
		sb.append("\n");
		sb.append(throwable.getClass().toString());
		sb.append("\n");

		for (int i = 0; i < throwable.getStackTrace().length; i++) {
			sb.append(throwable.getStackTrace()[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

}