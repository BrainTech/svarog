package pl.edu.fuw.fid.signalanalysis.wavelet;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ShannonWavelet implements MotherWavelet {

	@Override
	public double getHalfWidth() {
		return 5.0;
	}

	@Override
	public String getLabel() {
		return "Shannon";
	}

	@Override
	public double value(double t) {
		double PIt_2 = Math.PI * t / 2;
		double sinc = (Math.abs(PIt_2) > 1.0e-6) ? Math.sin(PIt_2) / PIt_2 : 1;
		return sinc * Math.cos(3*PIt_2);
	}

}
