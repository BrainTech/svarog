/* ErrorsDialog.java created 2007-09-19
 *
 */

package org.signalml.app.view.components.dialogs.errors;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;

import javax.swing.DefaultListModel;

import org.signalml.domain.montage.MontageException;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.MessageSourceResolvable;

/**
 * Dialog for showing an exception.
 *
 * @author Piotr Szachewicz
 */
public class ExceptionDialog extends AbstractErrorsDialog  {

	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public ExceptionDialog(Window w, boolean isModal) {
		super(w, isModal);
		setTitle(_("Exception occurred!"));
	}

	/**
	 * Depending on the type of the {@code model}:
	 * <ul>
	 * <li>for {@link Errors}:<ul>
	 * <li>obtains the list of errors and converts it to an array,</li>
	 * <li>using this array creates the {@link ErrorListModel model} for the
	 * list of errors and sets it,</li></ul></li>
	 * <li>for {@link MessageSourceResolvable}:
	 * <ul>
	 * <li>creates the array containing one element - this model,</li>
	 * <li>using this array creates the {@link ErrorListModel model} for the
	 * list of errors and sets it.</li></ul></li>
	 * </ul>
	 */
	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {

		Throwable exception = (Throwable) model;

		DefaultListModel listModel = new DefaultListModel();
		listModel.addElement(exception.getMessage());
		errorList.setModel(listModel);
	}

	/**
	 * The model for this dialog must be either of type {@link Errors} or
	 * {@link MessageSourceResolvable}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return Throwable.class.isAssignableFrom(clazz);
	}

}