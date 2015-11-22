package pl.edu.fuw.fid.signalanalysis.wavelet;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;
import pl.edu.fuw.fid.signalanalysis.waveform.ImageRenderer;
import pl.edu.fuw.fid.signalanalysis.waveform.ImageRendererStatus;
import pl.edu.fuw.fid.signalanalysis.waveform.PreferencesWithAxes;
import pl.edu.fuw.fid.signalanalysis.SimpleSignal;
import pl.edu.fuw.fid.signalanalysis.waveform.ImageResult;
import pl.edu.fuw.fid.signalanalysis.waveform.Waveform;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ImageRendererForWavelet extends ImageRenderer<PreferencesForWavelet> {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ImageRendererForWavelet.class);

	private volatile MotherWavelet wavelet_ = new GaborWavelet();

	public ImageRendererForWavelet(SimpleSignal signal) {
		super(signal);
	}

	@Override
	public ImageResult compute(PreferencesWithAxes<PreferencesForWavelet> preferences, ImageRendererStatus status) throws Exception {
		final PreferencesForWavelet prefs = preferences.prefs;

		// długość okna zależy od maksymalnej długości falki
		int windowLength = 16;
		double fullWidth = 2 * prefs.wavelet.getHalfWidth();
		double maxScale = 1.0 / Math.min(preferences.yMin, preferences.yMax);
		while (maxScale * sampling * fullWidth > windowLength) {
			windowLength *= 2;
		}

		final double[] all = signal.getData();
		final Complex[][] windows = new Complex[preferences.height][];
		ImageResult result = new ImageResult(preferences.width, preferences.height);
		final FastFourierTransformer fft = new FastFourierTransformer();

		// prepare windows
		Complex[] window = new Complex[windowLength];
		for (int iy=0; iy<preferences.height; ++iy) {
			if (status.isCancelled()) {
				return null;
			}
			status.setProgress(0.25 * iy / preferences.height);
			double f = preferences.yAxis.getValueForDisplay(iy).doubleValue();
			result.f[iy] = f;
			Waveform scaled = prefs.wavelet.scale(f);
			double norm = 0.0;
			for (int ix=0; ix<windowLength; ++ix) {
				double t = (ix - 0.5*(windowLength-1)) / sampling;
				window[ix] = scaled.value(t);
				double re = window[ix].getReal(), im = window[ix].getImaginary();
				norm += re*re + im*im;
			}
			norm = 1.0 / Math.sqrt(norm * windowLength);
			for (int ix=0; ix<windowLength; ++ix) {
				window[ix] = window[ix].multiply(norm);
			}
			windows[iy] = fft.transform(window);
			for (int ix=0; ix<windowLength; ++ix) {
				windows[iy][ix] = windows[iy][ix].conjugate();
			}
		}

		for (int ix=0; ix<preferences.width; ++ix) {
			if (status.isCancelled()) {
				return null;
			}
			status.setProgress(0.25 + 0.75 * ix / preferences.width);
			double t = preferences.xAxis.getValueForDisplay(ix).doubleValue();
			result.t[ix] = t;
			int i0 = (int) Math.floor(sampling * t) - windowLength / 2;
			for (int wi=0; wi<windowLength; ++wi) {
				int i = i0 + wi;
				window[wi] = new Complex((i >=0 && i < all.length) ? all[i] : 0.0, 0.0);
			}
			Complex[] spectrum = fft.transform(window);
			for (int iy=0; iy<preferences.height; ++iy) {
				Complex sum = Complex.ZERO;
				for (int iw=0; iw<windowLength; ++iw) {
					sum = sum.add(spectrum[iw].multiply(windows[iy][iw]));
				}
				result.values[ix][iy] = sum.multiply(2.0);
			}
		}
		return result;
	}

	@Override
	protected PreferencesForWavelet getPreferences() {
		PreferencesForWavelet prefs = new PreferencesForWavelet();
		prefs.wavelet = wavelet_;
		return prefs;
	}

	public MotherWavelet getWavelet() {
		return wavelet_;
	}

	public void setWavelet(MotherWavelet wavelet) {
		wavelet_ = wavelet;
	}

}
