/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.fuw.fid.signalanalysis.stft;

import org.apache.commons.math.complex.Complex;
import org.signalml.math.fft.WindowFunction;
import org.signalml.math.fft.WindowType;
import pl.edu.fuw.fid.signalanalysis.waveform.Waveform;

/**
 *
 * @author piotr
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
