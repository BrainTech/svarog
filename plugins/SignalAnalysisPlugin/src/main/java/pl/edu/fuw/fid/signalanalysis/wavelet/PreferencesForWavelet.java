package pl.edu.fuw.fid.signalanalysis.wavelet;

/**
 * @author ptr@mimuw.edu.pl
 */
public class PreferencesForWavelet {

	MotherWavelet wavelet;
	boolean logScale;

	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof PreferencesForWavelet) {
			PreferencesForWavelet prefs = (PreferencesForWavelet) other;
			return wavelet.equals(prefs.wavelet) && (logScale == prefs.logScale);
		} else {
			return false;
		}
	}

}
