package org.signalml.plugin.fftsignaltool.dialogs;

import java.awt.event.ActionEvent;

import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.fftsignaltool.SignalFFTSettings;

/**
 * Action which shows the {@link SignalFFTSettingsDialog dialog} in which the
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
	private SignalFFTSettingsDialog fftSettingsDialog;

	/**
	 * Constructor. Sets the source of messages, {@link SignalFFTSettings FFT
	 * settings} and the text of the button.
	 * @param messageSource the source of messages (labels)
	 * @param settings the settings that are filled in the 
	 * {@link SignalFFTSettingsDialog dialog} shown by this action
	 */
	public  SignalFFTSettingsDialogAction( SignalFFTSettings settings) {
		super();
		fftSettings = settings;
		fftSettingsDialog = new SignalFFTSettingsDialog();
		setText("signalFFTSettings.buttonTitle");
	}
	
	/**
	 * Shows the {@link SignalFFTSettingsDialog dialog} in which the
	 * {@link SignalFFTSettings FFT settings} are filled.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		fftSettingsDialog.showDialog(fftSettings);

	}

}
