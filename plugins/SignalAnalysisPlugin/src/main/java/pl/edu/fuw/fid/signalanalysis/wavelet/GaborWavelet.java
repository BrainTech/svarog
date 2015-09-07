package pl.edu.fuw.fid.signalanalysis.wavelet;

import org.apache.commons.math.complex.Complex;

/**
 * @author ptr@mimuw.edu.pl
 */
public class GaborWavelet extends MotherWavelet {

	@Override
	public double getBasicFrequency() {
		return 1.0;
	}

	@Override
	public double getHalfWidth() {
		return 3.0;
	}

	@Override
	public String getLabel() {
		return "Gabor";
	}

	@Override
	public Complex value(double t) {
		double exp = Math.exp(-t*t);
		return new Complex(0.0, 2*Math.PI*t).exp().multiply(exp);
	}

}
