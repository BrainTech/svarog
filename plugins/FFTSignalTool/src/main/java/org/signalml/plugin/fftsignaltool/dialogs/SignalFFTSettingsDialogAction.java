package org.signalml.plugin.fftsignaltool.dialogs;

import java.awt.event.ActionEvent;

import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.fftsignaltool.SignalFFTSettings;
import static org.signalml.plugin.fftsignaltool.FFTSignalTool._;

/**
 * Action which shows the {@link SignalFFTSettingsPopupDialog dialog} in which the
 * {@link SignalFFTSettings FFT settings} are filled.
 *
 * @author Marcin Szumski
 */
public class SignalFFTSettingsDialogAction extends AbstractSignalMLAction {

	/**
	 * the serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the settings that are filled in {@link #fftSettingsDialog}
	 */
	private SignalFFTSettings fftSettings;

	/**
	 * the dialog that is shown by this action
	 */
	private SignalFFTSettingsPopupDialog fftSettingsDialog;

	/**
	 * Constructor. Sets the source of messages, {@link SignalFFTSettings FFT
	 * settings} and the text of the button.
	 * @param settings the settings that are filled in the
	 * {@link SignalFFTSettingsPopupDialog dialog} shown by this action
	 */
	public SignalFFTSettingsDialogAction(SignalFFTSettings settings) {
		super();
		fftSettings = settings;
		fftSettingsDialog = new SignalFFTSettingsPopupDialog(null, true);
		setText(_("Signal FFT Settings"));
	}

	/**
	 * Shows the {@link SignalFFTSettingsPopupDialog dialog} in which the
	 * {@link SignalFFTSettings FFT settings} are filled.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		fftSettingsDialog.showDialog(fftSettings);
	}

}
