package pl.edu.fuw.fid.signalanalysis.wavelet;

/**
 * @author ptr@mimuw.edu.pl
 */
public class PreferencesForWavelet {

	MotherWavelet wavelet;

	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof PreferencesForWavelet) {
			PreferencesForWavelet prefs = (PreferencesForWavelet) other;
			return wavelet.getClass().equals(prefs.wavelet.getClass());
		} else {
			return false;
		}
	}

}
