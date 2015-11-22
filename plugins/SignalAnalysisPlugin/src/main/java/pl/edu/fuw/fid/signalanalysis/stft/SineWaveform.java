package pl.edu.fuw.fid.signalanalysis.stft;

import org.apache.commons.math.complex.Complex;
import pl.edu.fuw.fid.signalanalysis.waveform.Waveform;

/**
 * @author ptr@mimuw.edu.pl
 */
public class SineWaveform implements Waveform {

	private final double frequency;
	private final double halfWidth;

	public SineWaveform(double frequency, double halfWidth) {
		this.frequency = frequency;
		this.halfWidth = halfWidth;
	}

	@Override
	public double getHalfWidth() {
		return halfWidth;
	}

	@Override
	public Complex value(double t) {
		return new Complex(0.0, 2*Math.PI*frequency*t).exp();
	}

}
