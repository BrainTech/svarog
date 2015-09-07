package pl.edu.fuw.fid.signalanalysis.waveform;

import java.awt.image.BufferedImage;
import javafx.scene.chart.Axis;
import org.apache.commons.math.complex.Complex;
import org.signalml.app.view.book.wignermap.WignerMapPalette;
import pl.edu.fuw.fid.signalanalysis.SimpleSignal;

/**
 * @author ptr@mimuw.edu.pl
 */
public abstract class ImageRenderer<P> {

	protected final SimpleSignal signal;
	protected final double sampling;

	private volatile WignerMapPalette palette_ = WignerMapPalette.RAINBOW;
	private volatile CachedImageResult<PreferencesWithAxes<P>> cache_;

	public ImageRenderer(SimpleSignal signal) {
		this.signal = signal;
		this.sampling = signal.getSamplingFrequency();
	}

	protected abstract ImageResult compute(PreferencesWithAxes<P> preferences, ImageRendererStatus status) throws Exception;

	public WignerMapPalette getPaletteType() {
		return palette_;
	}

	public TimeFrequency getTimeFrequency(int x, int y) {
		TimeFrequency result = null;
		CachedImageResult<PreferencesWithAxes<P>> cache = cache_;
		if (cache != null && x >= 0 && x < cache.result.t.length && y >= 0 && y < cache.result.f.length) {
			double t = cache.result.t[x];
			double f = cache.result.f[y];
			Complex v = cache.result.values[x][y];
			result = new TimeFrequency(t, f, v);
		}
		return result;
	}

	public void setPaletteType(WignerMapPalette palette) {
		palette_ = palette;
	}

	protected abstract P getPreferences();

	/**
	 * Render image consisting of computation result.
	 * This method will be run in background thread, so it can have a while.
	 * Implementation should periodically check if status.isCancelled()
	 * and abort (return null) if so.
	 *
	 * @param xAxis   values for x dimension
	 * @param yAxis   values for y dimension
	 * @param status  status to be checked periodically
	 * @return  computation result as two-dimensional array
	 * @throws Exception if computational error occurs
	 */
	public BufferedImage renderImage(Axis<Number> xAxis, Axis<Number> yAxis, ImageRendererStatus status) throws Exception {
		ImageResult result;
		PreferencesWithAxes<P> pax = new PreferencesWithAxes<P>(getPreferences(), xAxis, yAxis);
		CachedImageResult<PreferencesWithAxes<P>> cache = cache_;
		if (cache != null && cache.preferences.equals(pax)) {
			result = cache.result;
		} else {
			result = compute(pax, status);
			if (result == null) {
				return null;
			}
			cache_ = new CachedImageResult<PreferencesWithAxes<P>>(pax, result);
		}

		BufferedImage image = new BufferedImage(pax.width, pax.height, BufferedImage.TYPE_INT_RGB);
		if (result == null) {
			return null;
		}
		double max = 0;
		for (int ix=0; ix<pax.width; ++ix) {
			for (int iy=0; iy<pax.height; ++iy) {
				max = Math.max(max, result.values[ix][iy].abs());
			}
		}
		int[] palette = palette_.getPalette();
		for (int ix=0; ix<pax.width; ++ix) {
			if (status.isCancelled()) {
				return null;
			}
			for (int iy=0; iy<pax.height; ++iy) {
				double t = result.values[ix][iy].abs() / max;
				int value = palette[Math.min( (int) Math.floor(t * palette.length), palette.length-1 )];
				image.setRGB(ix, iy, value);
			}
		}
		return image;
	}

}
