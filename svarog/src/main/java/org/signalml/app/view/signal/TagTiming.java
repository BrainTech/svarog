package org.signalml.app.view.signal;

/**
 * Simple data structure with tag's position (start) and length.
 * Used mainly in SignalPlot and StaticRenderingHelper.
 */
class TagTiming {

	public final double position; // seconds
	public final double length; // seconds

	TagTiming(double position, double length) {
		this.position = position;
		this.length = length;
	}
}
