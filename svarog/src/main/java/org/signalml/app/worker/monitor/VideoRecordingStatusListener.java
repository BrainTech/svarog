package org.signalml.app.worker.monitor;

/**
 * Simple listener notified about the status (on/off) of the video recording process.
 *
 * @author piotr.rozanski@braintech.pl
 */
public interface VideoRecordingStatusListener {

	/**
	 * Called whenever the video recording status may have changed.
	 * There is no guarantee that the new status actually differs from a previous one.
	 *
	 * @param recording  current (new) status of the recording
	 */
	public void videoRecordingStatusChanged(boolean recording);

}
