package pl.edu.fuw.fid.signalanalysis.stft;

import java.awt.image.BufferedImage;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;
import org.signalml.math.fft.WindowFunction;
import org.signalml.math.fft.WindowType;
import pl.edu.fuw.fid.signalanalysis.ImageRenderer;
import pl.edu.fuw.fid.signalanalysis.ImageRendererStatus;
import pl.edu.fuw.fid.signalanalysis.SimpleSignal;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ImageRendererForSTFT implements ImageRenderer {

	private final SimpleSignal signal;

	private volatile WindowType windowType_ = WindowType.RECTANGULAR;
	private volatile Integer windowLength_ = 128;

	public ImageRendererForSTFT(SimpleSignal signal) {
		this.signal = signal;
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
	public BufferedImage renderImage(int width, int height, double tMin, double tMax, double fMin, double fMax, ImageRendererStatus status) throws Exception {
		final WindowType windowType = windowType_;
		final Integer windowLength = windowLength_;

		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final double sampling = signal.getSamplingFrequency();
		final double[] all = signal.getData();
		final double[] window = new double[windowLength];
		final double[][] result = new double[width][height];
		final FastFourierTransformer fft = new FastFourierTransformer();

		double max = 0;
		WindowFunction wf = new WindowFunction(windowType, windowType.getParameterDefault());
		for (int ix=0; ix<width; ++ix) {
			if (status.isCancelled()) {
				return null;
			}
			double t = tMin + (tMax - tMin) * ix / width;
			int i0 = (int) Math.floor(sampling * t) - windowLength / 2;
			for (int wi=0; wi<windowLength; ++wi) {
				int i = i0 + wi;
				window[wi] = (i >=0 && i < all.length) ? all[i] : 0.0;
			}
			double[] windowed = wf.applyWindow(window);
			Complex[] spectrum = fft.transform(windowed);
			for (int iy=0; iy<height; ++iy) {
				double f = fMin + (fMax - fMin) * iy / height;
				int i = (int) Math.round(windowLength * f / sampling);
				double value = (i >= 0 && i < windowLength) ? spectrum[i].abs() : 0.0;
				result[ix][iy] = value;
				max = Math.max(max, value);
			}
		}
		for (int ix=0; ix<width; ++ix) {
			if (status.isCancelled()) {
				return null;
			}
			for (int iy=0; iy<height; ++iy) {
				int value = (int) Math.floor(255 * result[ix][iy] / max);
				image.setRGB(ix, iy, 0x010101 * value);
			}
		}
		return image;
	}

}
