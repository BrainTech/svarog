package org.signalml.app.view.common.dialogs.errors;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Container;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.signalml.app.SvarogApplication;

public class Dialogs {

	/**
	 * An enum containing possible user responses in a dialog.
	 */
	public static enum DIALOG_OPTIONS { YES, NO }

	/**
	 * Shows a warning/confirmation dialog with Yes/No buttons.
	 * @param warningCode the code of the message shown in the dialog
	 * @return the button user pressed in the dialog
	 */
	public static Dialogs.DIALOG_OPTIONS showWarningYesNoDialog(String warning) {
		Object[] options = {_("Yes"), _("No")};

		int selectedIndex = JOptionPane.showOptionDialog(null,
							warning,
							_("Warning"),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE,
							null,
							options,
							options[1]);

		if (selectedIndex == 0)
			return Dialogs.DIALOG_OPTIONS.YES;
		else
			return Dialogs.DIALOG_OPTIONS.NO;

	}

	/**
	 * Shows a simple dialog with OK button showing the specified error message.
	 * @param message the error
	 */
	public static void showError(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Window dialogParent = SvarogApplication.getSharedInstance().getViewerElementManager().getDialogParent();
				JOptionPane.showMessageDialog(dialogParent, message, _("Error"), JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	/**
	 * Shows a simple dialog showing the specified information message.
	 * @param title the title of the dialog
	 * @param message the message shown in the dialog
	 */
	public static void showMessage(final String title, final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Window dialogParent = SvarogApplication.getSharedInstance().getViewerElementManager().getDialogParent();
				JOptionPane.showMessageDialog(dialogParent, message, title, JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

	/**
	 * Shows a simple dialog showing the specified warning message.
	 * @param title the title of the dialog
	 * @param message the message shown in the dialog
	 */
	public static void showWarningMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Window dialogParent = SvarogApplication.getSharedInstance().getViewerElementManager().getDialogParent();
				JOptionPane.showMessageDialog(dialogParent, message, _("Warning"), JOptionPane.WARNING_MESSAGE);
			}
		});
	}

	/**
	 * Shows the dialog with the provided exception.
	 * The dialog is shown in the Event Dispatching Thread.
	 * @param c the component from which the parent window for this dialog
	 * will be retrieved
	 * @param t the exception to be displayed
	 */
	public static void showExceptionDialog(final JComponent c, final Throwable t) {

		Window w = null;

		if (c != null) {
			Container cont = c.getTopLevelAncestor();
			if (cont instanceof Window) {
				w = (Window) cont;
			}
		}
		showExceptionDialog(w, t);
	}

	/**
	 * Shows an exception dialog describing the provided exception.
	 * The dialog is shown in the Event Dispatching Thread.
	 * @param w the parent window or null if there is no parent
	 * @param t the exception to be displayed
	 */
	public static void showExceptionDialog(final Window w, final Throwable t) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				ExceptionDialog dialog = new ExceptionDialog(w);
				dialog.showDialog(t);
			}
		});
	}

	/**
	 * Shows an exception dialog describing the provided exception.
	 * The dialog is shown in the Event Dispatching Thread.
	 * @param w the parent window or null if there is no parent
	 * @param t the exception to be displayed
	 */
	public static void showExceptionDialog(final Throwable t) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Dialogs.showExceptionDialog((JComponent)null, t);
			}
		});
	}
}
