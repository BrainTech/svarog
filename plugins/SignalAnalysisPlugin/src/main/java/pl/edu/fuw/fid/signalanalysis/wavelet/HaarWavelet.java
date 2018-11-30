package pl.edu.fuw.fid.signalanalysis.wavelet;

import org.apache.commons.math.complex.Complex;
import static org.signalml.plugin.i18n.PluginI18n._;

/**
 * Implementation of the Haar wavelet.
 * This wavelet is implemented primarily for educational purposed,
 * as it is not suited for continuous wavelet transform.
 *
 * @author ptr@mimuw.edu.pl
 */
public class HaarWavelet extends MotherWavelet {

	@Override
	public double getBasicFrequency() {
		return 1.0;
	}

	@Override
	public double getHalfWidth() {
		return 0.5;
	}

	@Override
	public String getLabel() {
		return _("Haar");
	}

	@Override
	public Complex value(double t) {
		if (t >= -0.5 && t < 0) {
			return Complex.ONE;
		}
		if (t >= 0 && t < 0.5) {
			return Complex.ONE.multiply(-1);
		}
		return Complex.ZERO;
	}

}
