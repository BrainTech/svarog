package pl.edu.fuw.fid.signalanalysis.waveform;

/**
 * @author ptr@mimuw.edu.pl
 * @param <P>
 */
public class PreferencesWithAxes<P> {

	public final P prefs;
	public final int width, height;
	public final double xMin, xMax, yMin, yMax;

	public PreferencesWithAxes(P prefs, int width, int height, double xMin, double xMax, double yMin, double yMax) {
		this.prefs = prefs;
		this.width = width;
		this.height = height;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}

	public boolean equals(PreferencesWithAxes<P> other) {
		return prefs.equals(other.prefs)
			&& (width == other.width)
			&& (height == other.height)
			&& (xMin == other.xMin)
			&& (xMax == other.xMax)
			&& (yMin == other.yMin)
			&& (yMax == other.yMax);
	}

}
