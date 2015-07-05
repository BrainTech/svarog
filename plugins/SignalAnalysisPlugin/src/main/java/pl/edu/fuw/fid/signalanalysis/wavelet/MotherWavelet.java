package pl.edu.fuw.fid.signalanalysis.wavelet;

/**
 * @author ptr@mimuw.edu.pl
 */
public interface MotherWavelet {

	public double getHalfWidth();

	public String getLabel();

	public double value(double t);

}
