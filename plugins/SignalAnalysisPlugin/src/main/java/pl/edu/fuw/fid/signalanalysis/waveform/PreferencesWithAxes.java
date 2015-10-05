package pl.edu.fuw.fid.signalanalysis.waveform;

import javafx.scene.chart.Axis;

/**
 * @author ptr@mimuw.edu.pl
 * @param <P>
 */
public class PreferencesWithAxes<P> {

	public final P prefs;
	public final Axis<Number> xAxis;
	public final Axis<Number> yAxis;
	public final int width, height;
	public final double xMin, xMax, yMin, yMax;

	public PreferencesWithAxes(P prefs, Axis<Number> xAxis, Axis<Number> yAxis) {
		this.prefs = prefs;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.width = (int) xAxis.getWidth();
		this.height = (int) yAxis.getHeight();
		this.xMin = xAxis.getValueForDisplay(0).doubleValue();
		this.xMax = xAxis.getValueForDisplay(width).doubleValue();
		this.yMin = yAxis.getValueForDisplay(0).doubleValue();
		this.yMax = yAxis.getValueForDisplay(height).doubleValue();
	}

	public boolean equals(PreferencesWithAxes<P> other) {
		return prefs.equals(other.prefs)
			&& yAxis == other.yAxis
			&& width == other.width
			&& height == other.height
			&& xMin == other.xMin
			&& xMax == other.xMax
			&& yMin == other.yMin
			&& yMax == other.yMax;
	}

}
