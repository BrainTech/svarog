package org.signalml.app.view.components.dialogs.errors;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.plugin.export.SignalMLException;

/**
 * Dialog for showing {@link ValidationErrors}.
 * 
 * @author Piotr Szachewicz
 */
public class ValidationErrorsDialog extends AbstractErrorsDialog {

	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public ValidationErrorsDialog(Window w, boolean isModal) {
		super(w, isModal);
		setTitle(_("Errors in data"));
	}

	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {
		
		if (model instanceof ValidationErrors) {
			ValidationErrors errors = (ValidationErrors) model;
			errorList.setModel(errors);
		}
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return ValidationErrors.class.isAssignableFrom(clazz);
	}

}
