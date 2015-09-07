package pl.edu.fuw.fid.signalanalysis.waveform;

import org.apache.commons.math.complex.Complex;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ImageResult {

	public final double[] t;
	public final double[] f;
	public final Complex[][] values;

	public ImageResult(int tSize, int fSize) {
		t = new double[tSize];
		f = new double[fSize];
		values = new Complex[tSize][fSize];
	}

}
