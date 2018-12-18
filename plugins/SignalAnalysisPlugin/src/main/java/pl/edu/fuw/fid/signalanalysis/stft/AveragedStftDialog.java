package pl.edu.fuw.fid.signalanalysis.stft;

import java.awt.GridLayout;
import java.awt.Window;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.math.fft.WindowType;
import org.signalml.plugin.export.SignalMLException;
import pl.edu.fuw.fid.signalanalysis.SignalAnalysisTools;
import pl.edu.fuw.fid.signalanalysis.waveform.AveragedBaseDialog;
import static org.signalml.plugin.i18n.PluginI18n._;

/**
 * Dialog for selecting settings for averaged Short-Time Fourier Transform.
 * Inherits some options from AveragedBaseDialog, and adds some new as well.
 *
 * @author ptr@mimuw.edu.pl
 */
public class AveragedStftDialog extends AveragedBaseDialog<PreferencesForSTFT> {

	private JComboBox windowTypeComboBox;
	private JComboBox windowLengthComboBox;

	public AveragedStftDialog(Window w, Boolean isModal) {
		super(w, isModal);
		setTitle(_("Averaged Short-Time Fourier Transform"));
	}

	@Override
	protected JPanel createCustomPanel() {
		WindowType[] windowTypes = WindowType.values();
		Arrays.sort(windowTypes, new Comparator<WindowType>() {
			@Override
			public int compare(WindowType o1, WindowType o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		windowTypeComboBox = new JComboBox(windowTypes);
		windowLengthComboBox = new JComboBox(new Integer[] { 32, 64, 128, 256, 512, 1024 });
		windowLengthComboBox.setEditable(true);

		JPanel fftPanel = new JPanel(new GridLayout(0, 2));
		fftPanel.add(new JLabel(_("window type:")));
		fftPanel.add(windowTypeComboBox);
		fftPanel.add(new JLabel(_("window size:")));
		fftPanel.add(windowLengthComboBox);

		return fftPanel;
	}

	@Override
	protected PreferencesForSTFT getCustomPreferences() {
		PreferencesForSTFT prefs = new PreferencesForSTFT();
		prefs.padToHeight = true;
		prefs.windowLength = SignalAnalysisTools.parsePositiveInteger(windowLengthComboBox.getSelectedItem());
		prefs.windowType = (WindowType) windowTypeComboBox.getSelectedItem();
		return prefs;
	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		super.validateDialog(model, errors);
		Integer windowLength = SignalAnalysisTools.parsePositiveInteger(windowLengthComboBox.getSelectedItem());
		if (windowLength == null) {
			errors.addError(_("Window length is invalid"));
		}
	}

}
