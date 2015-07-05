package pl.edu.fuw.fid.signalanalysis.stft;

import org.signalml.math.fft.WindowType;

/**
 * @author ptr@mimuw.edu.pl
 */
public class PreferencesForSTFT {

	WindowType windowType;
	int windowLength;
	boolean padToHeight;

	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof PreferencesForSTFT) {
			PreferencesForSTFT prefs = (PreferencesForSTFT) other;
			return windowType == prefs.windowType
				&& windowLength == prefs.windowLength
				&& padToHeight == prefs.padToHeight;

		} else {
			return false;
		}
	}

}
