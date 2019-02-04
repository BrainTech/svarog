package pl.edu.fuw.fid.signalanalysis;

/**
 * Single-channel signal interface.
 *
 * @author ptr@mimuw.edu.pl
 */
public interface SingleSignal {

	public void getSamples(int start, int length, double[] buffer);

	public double getSamplingFrequency();

}
