package org.signalml.app.view.common.dialogs;

/**
 * Interface for a listener for the results of a dialog.
 * Contains one function called when the dialog is completed
 * (with the boolean indicating if the dialog was successful).
 *
 */
public interface DialogResultListener {

	/**
	 * The function called when the dialog is completed.
	 * @param success {@code true} if the dialog was successful,
	 * {@code false} otherwise
	 */
	public abstract void dialogCompleted(boolean success);

}
