package pl.edu.fuw.fid.signalanalysis.waveform;

import org.apache.commons.math.complex.Complex;

/**
 * @author ptr@mimuw.edu.pl
 */
public interface Waveform {

	public double getHalfWidth();

	public Complex value(double t);

}
