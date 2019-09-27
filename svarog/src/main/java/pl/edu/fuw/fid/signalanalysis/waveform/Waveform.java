package pl.edu.fuw.fid.signalanalysis.waveform;

import org.apache.commons.math.complex.Complex;

/**
 * Interface for generic waveforms with bounded support.
 *
 * @author ptr@mimuw.edu.pl
 */
public interface Waveform {

	public double getHalfWidth();

	public Complex value(double t);

}
