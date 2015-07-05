package pl.edu.fuw.fid.signalanalysis.wavelet;

/**
 * @author ptr@mimuw.edu.pl
 */
public class MexicanHatWavelet implements MotherWavelet {

	private final static double NORM = 1.0 / Math.sqrt(2*Math.PI);

	@Override
	public double getHalfWidth() {
		return 4.0;
	}

	@Override
	public String getLabel() {
		return "mexican hat";
	}

	@Override
	public double value(double t) {
		return NORM * (1.0-t*t) * Math.exp(-0.5*t*t);
	}

}
