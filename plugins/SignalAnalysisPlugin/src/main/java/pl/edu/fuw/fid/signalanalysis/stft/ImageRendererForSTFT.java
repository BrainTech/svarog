package pl.edu.fuw.fid.signalanalysis.stft;

import java.util.Arrays;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;
import org.signalml.math.fft.WindowFunction;
import org.signalml.math.fft.WindowType;
import pl.edu.fuw.fid.signalanalysis.AsyncStatus;
import pl.edu.fuw.fid.signalanalysis.waveform.ImageRenderer;
import pl.edu.fuw.fid.signalanalysis.waveform.PreferencesWithAxes;
import pl.edu.fuw.fid.signalanalysis.SingleSignal;
import pl.edu.fuw.fid.signalanalysis.waveform.ImageResult;

/**
 * Computes Short-Time Fourier Transform coefficients
 * for parameters selected by the user.
 *
 * @author ptr@mimuw.edu.pl
 */
public class ImageRendererForSTFT extends ImageRenderer<PreferencesForSTFT> {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ImageRendererForSTFT.class);

	private volatile boolean padToHeight_ = false;
	private volatile Integer windowLength_ = 128;
	private volatile WindowType windowType_ = WindowType.BARTLETT;

	public ImageRendererForSTFT(SingleSignal signal) {
		super(signal);
	}

	@Override
	protected PreferencesForSTFT getPreferences() {
		PreferencesForSTFT prefs = new PreferencesForSTFT();
		prefs.padToHeight = padToHeight_;
		prefs.windowLength = windowLength_;
		prefs.windowType = windowType_;
		return prefs;
	}

	public boolean getPadToHeight() {
		return padToHeight_;
	}

	public void setPadToHeight(boolean padToHeight) {
		padToHeight_ = padToHeight;
	}

	public void setWindowType(WindowType windowType) {
		if (windowType != null) {
			windowType_ = windowType;
		}
	}

	public WindowType getWindowType() {
		return windowType_;
	}

	public void setWindowLength(Integer windowLength) {
		if (windowLength > 0) {
			windowLength_ = windowLength;
		}
	}

	public Integer getWindowLength() {
		return windowLength_;
	}

	private static double calculatePaddedHeight(int chartHeight, double minFrequency, double maxFrequency, double samplingFrequency) {
		return chartHeight * samplingFrequency / Math.abs(maxFrequency - minFrequency);
	}

	@Override
	protected ImageResult compute(PreferencesWithAxes<PreferencesForSTFT> preferences, AsyncStatus status) throws Exception {
		final PreferencesForSTFT prefs = preferences.prefs;
		final ImageResult result = new ImageResult(preferences.width, preferences.height, "Averaged Short-Time Fourier Transform");
		final FastFourierTransformer fft = new FastFourierTransformer();

		double paddedLengthMin = prefs.windowLength;
		if (prefs.padToHeight) {
			paddedLengthMin = Math.max(
				paddedLengthMin,
				calculatePaddedHeight(preferences.height, preferences.yMin, preferences.yMax, signal.getSamplingFrequency())
			);
		}
		int paddedLength = 2;
		while (paddedLength < paddedLengthMin) {
			paddedLength *= 2;
		}
		WindowFunction wf = new WindowFunction(prefs.windowType, prefs.windowType.getParameterDefault());

		double[] chunk = new double[prefs.windowLength];
		double[] padded = new double[paddedLength];
		for (int ix=0; ix<preferences.width; ++ix) {
			if (status.isCancelled()) {
				return null;
			}
			status.setProgress(ix / (double) preferences.width);

			double t0 = preferences.xMin + (preferences.xMax - preferences.xMin) * ix / (preferences.width - 1);
			result.t[ix] = t0;
			int n0 = (int) Math.round(t0 * sampling);
			Arrays.fill(chunk, 0.0);
			signal.getSamples(n0 - prefs.windowLength / 2, prefs.windowLength, chunk);
			double[] windowed = wf.applyWindow(chunk);
			System.arraycopy(windowed, 0, padded, 0, prefs.windowLength);
			Complex[] spectrum = fft.transform(padded);
			for (int iy=0; iy<preferences.height; ++iy) {
				double fIdeal = preferences.yMin + (preferences.yMax - preferences.yMin) * iy / (preferences.height - 1);
				int i = (int) Math.round(spectrum.length * fIdeal / sampling);
				double fExact = i * sampling / spectrum.length;
				result.f[iy] = fExact;
				// phase difference between start and center of time window
				Complex phaser = new Complex(0, Math.PI*result.f[iy]*prefs.windowLength/sampling).exp().multiply(2.0);
				Complex value = (i >= 0 && i < paddedLength) ? spectrum[i].multiply(phaser) : Complex.ZERO;
				result.values[ix][iy] = value;
			}
		}
		return result;
	}

}
