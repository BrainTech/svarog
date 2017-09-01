/* SignalFFTSettingsPopupDialog.java created 2007-12-17
 *
 */

package org.signalml.plugin.fftsignaltool.dialogs;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPopupDialog;
import org.signalml.plugin.fftsignaltool.SignalFFTSettings;

import org.springframework.validation.Errors;

/**
 * Dialog which allows to select the parameters of the FFT.
 * Contains only {@link SignalFFTSettingsPanel}, where these parameters are
 * described.
 * <p>
 * The model for this dialog is of type {@link SignalFFTSettings} and the
 * parameters are stored in it.
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
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public SignalFFTSettingsPopupDialog(
		Window w, boolean isModal) {
		super(w, isModal);
	}

	/**
	 * Creates the interface for this dialog.
	 * This interface contains only {@link SignalFFTSettingsPanel}.
	 */
	@Override
	public JComponent createInterface() {

		signalFFTSettingsPanel = new SignalFFTSettingsPanel(true);

		return signalFFTSettingsPanel;

	}

	/**
	 * {@link SignalFFTSettingsPanel#fillPanelFromModel(org.signalml.plugin.
	 * fftsignaltool.SignalFFTSettings) Fills} the {@link
	 * SignalFFTSettingsPanel} from the {@link SignalFFTSettings model}.
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		SignalFFTSettings settings = (SignalFFTSettings) model;

		signalFFTSettingsPanel.fillPanelFromModel(settings);

	}

	/**
	 * {@link SignalFFTSettingsPanel#fillModelFromPanel(org.signalml.plugin.
	 * fftsignaltool.SignalFFTSettings) Fills} the {@link SignalFFTSettings model}.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		SignalFFTSettings settings = (SignalFFTSettings) model;

		signalFFTSettingsPanel.fillModelFromPanel(settings);
	}

	/**
	 * Validates this dialog.
	 * This dialog is valid if {@link SignalFFTSettingsPanel} is {@link
	 * SignalFFTSettingsPanel#validatePanel(Errors) valid}.
	 */
	@Override
	public void validateDialog(Object model, ValidationErrors errors)
	throws SignalMLException {
		super.validateDialog(model, errors);

		signalFFTSettingsPanel.validatePanel(errors);

	}

	/**
	 * The model for this dialog must be of type {@link SignalFFTSettings}
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SignalFFTSettings.class.isAssignableFrom(clazz);
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
