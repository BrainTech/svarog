/* SignalFFTSettingsPopupDialog.java created 2007-12-17
 *
 */

package org.signalml.plugin.fftsignaltool.dialogs;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPopupDialog;
import org.signalml.plugin.fftsignaltool.SignalFFTSettings;
import org.signalml.plugin.fftsignaltool.SignalFFTTool;

import org.springframework.validation.Errors;

/**
 * Dialog which allows to select the parameters of the FFT.
 * Contains only {@link SignalFFTSettingsPanel}, where these parameters are
 * described.
 * <p>
 * The model for this dialog is of type {@link SignalFFTTool} and the
 * parameters are stored in the {@link SignalFFTSettings settings}
 * {@link SignalFFTTool#getSettings() obtained} from it.
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class SignalFFTSettingsPopupDialog extends AbstractPopupDialog {

	/**
	 * the serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the {@link SignalFFTSettingsPanel panel} with the actual contents of
	 * this dialog
	 */
	private SignalFFTSettingsPanel signalFFTSettingsPanel;

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param messageSource message source to set
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public  SignalFFTSettingsPopupDialog(
			Window w, boolean isModal) {
		super( w, isModal);
	}

	/**
	 * Creates the interface for this dialog.
	 * This interface contains only {@link SignalFFTSettingsPanel}.
	 */
	@Override
	public JComponent createInterface() {

		signalFFTSettingsPanel = new SignalFFTSettingsPanel( true);

		return signalFFTSettingsPanel;

	}

	/**
	 * {@link SignalFFTSettingsPanel#fillPanelFromModel(org.signalml.plugin.
	 * fftsignaltool.SignalFFTSettings) Fills} the {@link
	 * SignalFFTSettingsPanel} from the {@link SignalFFTTool model}.
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		SignalFFTTool tool = (SignalFFTTool) model;

		signalFFTSettingsPanel.fillPanelFromModel(tool.getSettings());

	}

	/**
	 * {@link SignalFFTSettingsPanel#fillModelFromPanel(org.signalml.plugin.
	 * fftsignaltool.SignalFFTSettings) Fills} the {@link SignalFFTTool model}
	 * from the {@link SignalFFTSettingsPanel}.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		SignalFFTTool tool = (SignalFFTTool) model;

		signalFFTSettingsPanel.fillModelFromPanel(tool.getSettings());
	}

	/**
	 * Validates this dialog.
	 * This dialog is valid if {@link SignalFFTSettingsPanel} is {@link
	 * SignalFFTSettingsPanel#validatePanel(Errors) valid}.
	 */
	@Override
	public void validateDialog(Object model, Errors errors)
			throws SignalMLException {
		super.validateDialog(model, errors);

		errors.pushNestedPath("settings");
		signalFFTSettingsPanel.validatePanel(errors);
		errors.popNestedPath();

	}

	/**
	 * The model for this dialog must be of type {@link SignalFFTTool}
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SignalFFTTool.class.isAssignableFrom(clazz);
	}

	/**
	 * @return false
	 */
	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}

	/**
	 * @return false
	 */
	@Override
	public boolean isCancellable() {
		return false;
	}

	/**
	 * @return true
	 */
	@Override
	public boolean isFormClickApproving() {
		return true;
	}

}
