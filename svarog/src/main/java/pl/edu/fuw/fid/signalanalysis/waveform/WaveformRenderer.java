package pl.edu.fuw.fid.signalanalysis.waveform;

import java.util.Arrays;
import org.apache.commons.math.complex.Complex;
import org.signalml.math.fft.WindowFunction;
import org.signalml.math.fft.WindowType;

/**
 * Computes values of waveforms around some given time location.
 *
 * @author ptr@mimuw.edu.pl
 */
public class WaveformRenderer {

	public static double[][] compute(Waveform wf, double t0, WindowType windowType, Complex coeff, double samplingFrequency) {
		double hw = wf.getHalfWidth();
		double tStart = t0 - hw, tEnd = t0 + hw;
		int iStart = (int) Math.round(tStart * samplingFrequency - 0.5);
		int iEnd = (int) Math.round(tEnd * samplingFrequency + 0.5);
		int length = (int) (1 + iEnd - iStart);

		double[] ones = new double[length];
		Arrays.fill(ones, 1.0);
		if (windowType != null) {
			ones = new WindowFunction(windowType, windowType.getParameterDefault()).applyWindow(ones);
		}

		Complex[] values = new Complex[length];
		double sumSquare = 0.0;
		for (int i=iStart; i<=iEnd; ++i) {
			double t = i / samplingFrequency;
			Complex waveformValue = wf.value(t-t0).multiply(ones[i-iStart]);
			values[i-iStart] = waveformValue;

			double re = waveformValue.getReal();
			double im = waveformValue.getImaginary();
			sumSquare += re*re + im*im;
		}

		final Complex norm = coeff.multiply(1.0/Math.sqrt(sumSquare * samplingFrequency));

		double[][] result = new double[2][length];
		for (int i=iStart; i<=iEnd; ++i) {
			double t = i / samplingFrequency;
			double value = values[i-iStart].multiply(norm).getReal();
			result[0][i-iStart] = t;
			result[1][i-iStart] = value;
		}
		return result;
	}

}
