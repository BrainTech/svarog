package org.signalml.plugin.fftsignaltool.actions;

import java.awt.event.ActionEvent;

import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.fftsignaltool.SignalFFTSettings;
import org.signalml.plugin.fftsignaltool.dialogs.SettingsEdit;

import static org.signalml.plugin.fftsignaltool.FFTSignalPlugin._;

/**
 * Action which shows the {@link SettingsEdit dialog} in which the
 * {@link SignalFFTSettings FFT settings} are filled.
 *
 * @author Marcin Szumski
 */
public class ShowSettings extends AbstractSignalMLAction {

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
	private SettingsEdit fftSettingsDialog;

	/**
	 * Constructor. Sets the source of messages, {@link SignalFFTSettings FFT
	 * settings} and the text of the button.
	 * @param settings the settings that are filled in the
	 * {@link SettingsEdit dialog} shown by this action
	 */
	public ShowSettings(SignalFFTSettings settings) {
		super();
		fftSettings = settings;
		fftSettingsDialog = new SettingsEdit(null, true);
		setText(_("Signal FFT Settings"));
	}

	/**
	 * Shows the {@link SettingsEdit dialog} in which the
	 * {@link SignalFFTSettings FFT settings} are filled.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		fftSettingsDialog.showDialog(fftSettings);
	}

}
