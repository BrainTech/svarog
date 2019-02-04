package pl.edu.fuw.fid.signalanalysis.wavelet;

import org.apache.commons.math.complex.Complex;
import pl.edu.fuw.fid.signalanalysis.AsyncStatus;
import pl.edu.fuw.fid.signalanalysis.waveform.ImageRenderer;
import pl.edu.fuw.fid.signalanalysis.waveform.PreferencesWithAxes;
import pl.edu.fuw.fid.signalanalysis.SingleSignal;
import pl.edu.fuw.fid.signalanalysis.waveform.ImageResult;
import pl.edu.fuw.fid.signalanalysis.waveform.Waveform;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Computes Wavelet Transform coefficients for parameters selected by the user.
 *
 * @author ptr@mimuw.edu.pl
 */
public class ImageRendererForWavelet extends ImageRenderer<PreferencesForWavelet> {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ImageRendererForWavelet.class);

	private volatile MotherWavelet wavelet_ = new GaborWavelet();
	private volatile boolean logScale_ = false;

	public ImageRendererForWavelet(SingleSignal signal) {
		super(signal);
	}

	@Override
	public ImageResult compute(PreferencesWithAxes<PreferencesForWavelet> preferences, AsyncStatus status) throws Exception {
		final PreferencesForWavelet prefs = preferences.prefs;

		// długość okna zależy od maksymalnej długości falki
		int windowLength = 16;
		double fullWidth = 2 * prefs.wavelet.getHalfWidth();
		double maxScale = 1.0 / Math.min(preferences.yMin, preferences.yMax);
		while (maxScale * sampling * fullWidth > windowLength) {
			windowLength *= 2;
		}

		final Complex[][] windows = new Complex[preferences.height][];
		ImageResult result = new ImageResult(preferences.width, preferences.height, String.format(_("Averaged wavelet transform (%s)"), prefs.wavelet.getLabel()));

		// prepare windows
		Complex[] window = new Complex[windowLength];
		for (int iy=0; iy<preferences.height; ++iy) {
			if (status.isCancelled()) {
				return null;
			}
			status.setProgress(0.25 * iy / preferences.height);
			double f = prefs.logScale
				? Math.exp( Math.log(preferences.yMin) + Math.log(preferences.yMax / preferences.yMin) * iy / (preferences.height - 1) )
				: preferences.yMin + (preferences.yMax - preferences.yMin) * iy / (preferences.height - 1);
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
			windows[iy] = window.clone();
		}

		double[] chunk = new double[windowLength];
		for (int ix=0; ix<preferences.width; ++ix) {
			if (status.isCancelled()) {
				return null;
			}
			status.setProgress(0.25 + 0.75 * ix / preferences.width);
			double t = preferences.xMin + (preferences.xMax - preferences.xMin) * ix / (preferences.width - 1);
			result.t[ix] = t;
			int i0 = (int) Math.floor(sampling * t) - windowLength / 2;
			signal.getSamples(i0, windowLength, chunk);
			for (int iy=0; iy<preferences.height; ++iy) {
				Complex sum = Complex.ZERO;
				for (int iw=0; iw<windowLength; ++iw) {
					sum = sum.add(windows[iy][iw].multiply(chunk[iw]));
				}
				result.values[ix][iy] = sum.conjugate().multiply(windowLength);
			}
		}
		return result;
	}

	@Override
	protected PreferencesForWavelet getPreferences() {
		PreferencesForWavelet prefs = new PreferencesForWavelet();
		prefs.wavelet = wavelet_;
		prefs.logScale = logScale_;
		return prefs;
	}

	public MotherWavelet getWavelet() {
		return wavelet_;
	}

	public void setWavelet(MotherWavelet wavelet) {
		wavelet_ = wavelet;
	}
	
	public boolean isLogScale() {
		return logScale_;
	}
	
	public void setLogScale(boolean logScale) {
		logScale_ = logScale;
	}

}
