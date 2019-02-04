package pl.edu.fuw.fid.signalanalysis.waveform;

import org.apache.commons.math.complex.Complex;

/**
 * Time-frequency map (power estimation). Stores also time and frequency values
 * as well as text summary of the used method.
 *
 * @author ptr@mimuw.edu.pl
 */
public class ImageResult {

	public final double[] t;
	public final double[] f;
	public final Complex[][] values;
	public final String title;

	public ImageResult(int tSize, int fSize, String title) {
		t = new double[tSize];
		f = new double[fSize];
		values = new Complex[tSize][fSize];
		this.title = title;
	}

}
