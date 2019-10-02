package org.signalml.app.worker.monitor.recording;

/**
 * @author piotr.rozanski@braintech.pl
 */
public interface RecordingListener {

	public void recordingStateChanged(RecordingState state);

}
