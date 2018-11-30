package pl.edu.fuw.fid.signalanalysis.wavelet;

import org.apache.commons.math.complex.Complex;
import static org.signalml.plugin.i18n.PluginI18n._;

/**
 * Implementation of the complex Shannon wavelet.
 *
 * @author ptr@mimuw.edu.pl
 */
public class ShannonWavelet extends MotherWavelet {

	@Override
	public double getBasicFrequency() {
		return 1.0;
	}

	@Override
	public double getHalfWidth() {
		return 5.0;
	}

	@Override
	public String getLabel() {
		return _("Shannon");
	}

	@Override
	public Complex value(double t) {
		double sinc = (Math.abs(t) > 1.0e-6) ? Math.sin(t) / t : 1;
		return new Complex(0.0, 2*Math.PI*t).exp().multiply(sinc);
	}

}
