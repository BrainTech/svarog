package org.signalml.plugin.fftsignaltool.dialogs;

import javax.swing.JComponent;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.fftsignaltool.SignalFFTSettings;

/**
 * Dialog which allows to select the parameters of the FFT.
 * Contains only {@link SignalFFTSettingsPanel}, where these parameters are
 * described.
 * <p>
 * The model for this dialog is of type {@link SignalFFTSettings} and the
 * parameters are stored in it.
 *
 * @author Marcin Szumski
 */
public class SignalFFTSettingsDialog extends org.signalml.plugin.export.view.AbstractPluginDialog  {

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
	 * Constructor. Sets message source, that there is no parent window and
	 * that this dialog blocks top-level windows.
	 */
	public SignalFFTSettingsDialog() {
		super(null, true);
	}

	/**
	 * Creates the interface for this dialog.
	 * This interface contains only {@link SignalFFTSettingsPanel}.
	 */
	@Override
	protected JComponent createInterface() {
		signalFFTSettingsPanel = new SignalFFTSettingsPanel(true);

		return signalFFTSettingsPanel;
	}

	/**
	 * The model for this dialog must be of type {@link SignalFFTSettings}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SignalFFTSettings.class.isAssignableFrom(clazz);
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
	 * fftsignaltool.SignalFFTSettings) Fills} the {@link SignalFFTSettings
	 * model} from the {@link SignalFFTSettingsPanel}.
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

}
