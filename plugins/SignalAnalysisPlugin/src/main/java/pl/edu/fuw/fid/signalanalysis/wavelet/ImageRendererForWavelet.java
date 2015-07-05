package pl.edu.fuw.fid.signalanalysis.wavelet;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;
import pl.edu.fuw.fid.signalanalysis.ImageRenderer;
import pl.edu.fuw.fid.signalanalysis.ImageRendererStatus;
import pl.edu.fuw.fid.signalanalysis.PreferencesWithAxes;
import pl.edu.fuw.fid.signalanalysis.SimpleSignal;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ImageRendererForWavelet extends ImageRenderer<PreferencesForWavelet> {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ImageRendererForWavelet.class);

	private volatile MotherWavelet wavelet_ = new MexicanHatWavelet();

	public ImageRendererForWavelet(SimpleSignal signal) {
		super(signal);
	}

	public WaveletPreview computeWaveletPreview(double t0, double scale) {
		MotherWavelet wavelet = wavelet_;
		double t_start = t0 - scale * wavelet.getHalfWidth();
		double t_end = t0 + scale * wavelet.getHalfWidth();
		int i_start = (int) Math.round(t_start * sampling);
		int i_end = (int) Math.round(t_end * sampling);

		WaveletPreview preview = new WaveletPreview(t_start, t_end);
		double scaleSqrt = Math.sqrt(scale);
		final double[] all = signal.getData();
		double sumProduct = 0.0;
		double sumSquare = 0.0;
		final double norm = 1.0 / sampling; // integration step
		for (int i=i_start; i<=i_end; ++i) {
			double t = i / sampling;
			double waveletValue = wavelet.value((t-t0) / scale) / scaleSqrt;
			double signalValue = (i>=0 && i<all.length) ? all[i] : 0.0;
			preview.addDataPoint(t, waveletValue, signalValue);
			sumProduct += waveletValue * signalValue;
			sumSquare += waveletValue * waveletValue;
		}
		preview.scaleWavelet(sumProduct * Math.sqrt(norm / sumSquare));
		return preview;
	}

	@Override
	public double[][] computeValues(PreferencesWithAxes<PreferencesForWavelet> preferences, ImageRendererStatus status) throws Exception {
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
		final double[][] result = new double[preferences.width][preferences.height];
		final FastFourierTransformer fft = new FastFourierTransformer();

		// prepare windows
		double[] window = new double[windowLength];
		for (int iy=0; iy<preferences.height; ++iy) {
			if (status.isCancelled()) {
				return null;
			}
			status.setProgress(0.25 * iy / preferences.height);
			double scale = 1.0 / preferences.yAxis.getValueForDisplay(iy).doubleValue();
			double scaleSqrt = Math.sqrt(scale);
			for (int ix=0; ix<windowLength; ++ix) {
				double t = (ix - 0.5*(windowLength-1)) / sampling;
				window[ix] = prefs.wavelet.value(t / scale) / scaleSqrt;
			}
			windows[iy] = fft.transform(window);
			for (int ix=0; ix<windowLength; ++ix) {
				windows[iy][ix] = windows[iy][ix].conjugate();
			}
		}

		double max = 0;
		for (int ix=0; ix<preferences.width; ++ix) {
			if (status.isCancelled()) {
				return null;
			}
			status.setProgress(0.25 + 0.75 * ix / preferences.width);
			double t = preferences.xAxis.getValueForDisplay(ix).doubleValue();
			int i0 = (int) Math.floor(sampling * t) - windowLength / 2;
			for (int wi=0; wi<windowLength; ++wi) {
				int i = i0 + wi;
				window[wi] = (i >=0 && i < all.length) ? all[i] : 0.0;
			}
			Complex[] spectrum = fft.transform(window);
			for (int iy=0; iy<preferences.height; ++iy) {
				Complex sum = Complex.ZERO;
				for (int iw=0; iw<windowLength; ++iw) {
					sum = sum.add(spectrum[iw].multiply(windows[iy][iw]));
				}
				double value = sum.abs();
				result[ix][iy] = value;
				max = Math.max(max, value);
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
