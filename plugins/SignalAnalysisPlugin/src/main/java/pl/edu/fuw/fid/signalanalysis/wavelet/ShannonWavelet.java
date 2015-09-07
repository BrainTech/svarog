package pl.edu.fuw.fid.signalanalysis.wavelet;

import org.apache.commons.math.complex.Complex;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ShannonWavelet extends MotherWavelet {

	@Override
	public double getBasicFrequency() {
		return 0.75;
	}

	@Override
	public double getHalfWidth() {
		return 5.0;
	}

	@Override
	public String getLabel() {
		return "Shannon";
	}

	@Override
	public Complex value(double t) {
		double PIt_2 = Math.PI * t / 2;
		double sinc = (Math.abs(PIt_2) > 1.0e-6) ? Math.sin(PIt_2) / PIt_2 : 1;
		return new Complex(sinc * Math.cos(3*PIt_2), 0.0);
	}

}
