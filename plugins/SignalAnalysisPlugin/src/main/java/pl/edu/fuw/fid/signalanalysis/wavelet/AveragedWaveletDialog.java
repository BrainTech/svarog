package pl.edu.fuw.fid.signalanalysis.wavelet;

import java.awt.GridLayout;
import java.awt.Window;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.plugin.export.SignalMLException;
import pl.edu.fuw.fid.signalanalysis.SignalAnalysisTools;
import pl.edu.fuw.fid.signalanalysis.waveform.AveragedBaseDialog;
import pl.edu.fuw.fid.signalanalysis.waveform.PreferencesWithAxes;

/**
 * @author ptr@mimuw.edu.pl
 */
public class AveragedWaveletDialog extends AveragedBaseDialog<PreferencesForWavelet> {

	private JComboBox waveletTypeComboBox;
	private final MotherWavelet[] wavelets = new MotherWavelet[] {
		new GaborWavelet(1.0),
		new ShannonWavelet(),
		new HaarWavelet()
	};

	public AveragedWaveletDialog(Window w, Boolean isModal) {
		super(w, isModal);
		setTitle("Averaged Wavelet Transform");
	}

	@Override
	protected JPanel createCustomPanel() {
		String[] waveletNames = new String[wavelets.length];
		for (int i=0; i<wavelets.length; ++i) {
			waveletNames[i] = wavelets[i].getLabel();
		}
		waveletTypeComboBox = new JComboBox(waveletNames);

		JPanel wtPanel = new JPanel(new GridLayout(0, 1));
		wtPanel.add(new JLabel("wavelet type:"));
		wtPanel.add(waveletTypeComboBox);

		return wtPanel;
	}

	@Override
	protected PreferencesForWavelet getCustomPreferences() {
		PreferencesForWavelet prefs = new PreferencesForWavelet();
		prefs.wavelet = wavelets[waveletTypeComboBox.getSelectedIndex()];
		prefs.logScale = false;
		return prefs;
	}

	@Override
	protected PreferencesWithAxes<PreferencesForWavelet> getPreferences() {
		PreferencesWithAxes<PreferencesForWavelet> preferences = super.getPreferences();
		return new PreferencesWithAxes<PreferencesForWavelet>(
			preferences.prefs,
			preferences.width,
			preferences.height,
			preferences.xMin,
			preferences.xMax,
			Math.max(SignalAnalysisTools.MIN_WAVELET_FREQ, preferences.yMin),
			Math.max(SignalAnalysisTools.MIN_WAVELET_FREQ, preferences.yMax)
		);
	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		super.validateDialog(model, errors);
	}

}
