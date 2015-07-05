package pl.edu.fuw.fid.signalanalysis.stft;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;
import org.signalml.math.fft.WindowFunction;
import org.signalml.math.fft.WindowType;
import pl.edu.fuw.fid.signalanalysis.ImageRenderer;
import pl.edu.fuw.fid.signalanalysis.ImageRendererStatus;
import pl.edu.fuw.fid.signalanalysis.PreferencesWithAxes;
import pl.edu.fuw.fid.signalanalysis.SimpleSignal;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ImageRendererForSTFT extends ImageRenderer<PreferencesForSTFT> {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ImageRendererForSTFT.class);

	private volatile boolean padToHeight_ = false;
	private volatile Integer windowLength_ = 128;
	private volatile WindowType windowType_ = WindowType.RECTANGULAR;

	public ImageRendererForSTFT(SimpleSignal signal) {
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

	private static int calculatePaddedWindowLength(int windowLength, int chartHeight) {
		while (windowLength < chartHeight) {
			windowLength *= 2;
		}
		return windowLength;
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

	@Override
	protected double[][] computeValues(PreferencesWithAxes<PreferencesForSTFT> preferences, ImageRendererStatus status) throws Exception {
		final PreferencesForSTFT prefs = preferences.prefs;
		final int spectrumLength = prefs.padToHeight ? calculatePaddedWindowLength(prefs.windowLength, preferences.height) : prefs.windowLength;
		final double[] all = signal.getData();
		final double[] window = new double[prefs.windowLength];
		final double[][] result = new double[preferences.width][preferences.height];
		final FastFourierTransformer fft = new FastFourierTransformer();

		double max = 0;
		WindowFunction wf = new WindowFunction(prefs.windowType, prefs.windowType.getParameterDefault());
		for (int ix=0; ix<preferences.width; ++ix) {
			if (status.isCancelled()) {
				return null;
			}
			double t = preferences.xAxis.getValueForDisplay(ix).doubleValue();
			int i0 = (int) Math.floor(sampling * t) - prefs.windowLength / 2;
			for (int wi=0; wi<prefs.windowLength; ++wi) {
				int i = i0 + wi;
				window[wi] = (i >=0 && i < all.length) ? all[i] : 0.0;
			}
			double[] windowed = wf.applyWindow(window);
			if (spectrumLength > prefs.windowLength) {
				double[] temp = new double[spectrumLength];
				System.arraycopy(windowed, 0, temp, 0, prefs.windowLength);
				windowed = temp;
			}
			Complex[] spectrum = fft.transform(windowed);
			for (int iy=0; iy<preferences.height; ++iy) {
				double f = preferences.yAxis.getValueForDisplay(iy).doubleValue();
				int i = (int) Math.floor(spectrumLength * f / sampling);
				double value = (i >= 0 && i < spectrumLength) ? spectrum[i].abs() : 0.0;
				result[ix][iy] = value;
				max = Math.max(max, value);
			}
		}
		return result;
	}

}
