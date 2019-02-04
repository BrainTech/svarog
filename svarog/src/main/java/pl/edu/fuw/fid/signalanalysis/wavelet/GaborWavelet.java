package pl.edu.fuw.fid.signalanalysis.wavelet;

import org.apache.commons.math.complex.Complex;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Implementation of a Morlet (aka Gabor) wavelet.
 * This wavelet is implemented as <code>f(t) = exp(iwt) exp(-t²/2)</code>.
 *
 * @author ptr@mimuw.edu.pl
 */
public class GaborWavelet extends ParamWavelet {

	public static final double DEFAULT_WIDTH = 5.0;

	public GaborWavelet() {
		this(DEFAULT_WIDTH);
	}

	public GaborWavelet(Double w) {
		super(w);
	}

	@Override
	public double getBasicFrequency() {
		return 0.5*param/Math.PI;
	}

	@Override
	public double getHalfWidth() {
		return 0.5*param;
	}

	@Override
	public String getLabel() {
		return _("Morlet");
	}

	@Override
	public Complex value(double t) {
		double exp = Math.exp(-0.5*t*t);
		return new Complex(0.0, param*t).exp().multiply(exp);
	}

}
