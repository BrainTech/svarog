package org.signalml.app.view.signal;

/**
 * Interface to allow listening for user clicking on column header (time line).
 *
 * @author piotr.rozanski@braintech.pl
 */
public interface SignalPlotColumnHeaderListener {

	/**
	 * Called whenever user clicks on the timeline.
	 *
	 * @param time  selected time in seconds, from the beginning of the signal
	 */
	public void timeSelected(double time);

}
