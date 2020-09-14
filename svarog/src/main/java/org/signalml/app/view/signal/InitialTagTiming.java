package org.signalml.app.view.signal;

/**
 * Simple data structure with tag's position (start) and
 * a number of epoch in which it has been created.
 * Used internally in StaticRenderingHelper.
 */
class InitialTagTiming {

	public final double position;

	public final long cycleNumber;

	InitialTagTiming(double position, long cycleNumber) {
		this.position = position;
		this.cycleNumber = cycleNumber;
	}
}
