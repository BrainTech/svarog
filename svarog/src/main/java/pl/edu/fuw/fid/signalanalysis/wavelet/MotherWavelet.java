package pl.edu.fuw.fid.signalanalysis.wavelet;

import org.apache.commons.math.complex.Complex;
import pl.edu.fuw.fid.signalanalysis.waveform.Waveform;

/**
 * Base class for wavelet waveforms.
 *
 * @author ptr@mimuw.edu.pl
 */
public abstract class MotherWavelet implements Waveform {

	@Override
	abstract public double getHalfWidth();

	abstract public String getLabel();

	abstract public double getBasicFrequency();

	@Override
	abstract public Complex value(double t);

	public Waveform scale(final double f) {
		return new Waveform() {

			@Override
			public double getHalfWidth() {
				return MotherWavelet.this.getHalfWidth() / f;
			}

			@Override
			public Complex value(double t) {
				double relative = f / getBasicFrequency();
				return MotherWavelet.this.value(t * relative).multiply(Math.sqrt(relative));
			}

		};
	}

	public static MotherWavelet get(String name, double param) {
		if (name.equals("Morlet")) {
			return new GaborWavelet(param);
		}
		if (name.equals("Shannon")) {
			return new ShannonWavelet();
		}
		if (name.equals("Haar")) {
			return new HaarWavelet();
		}
		return null;
	}
}
