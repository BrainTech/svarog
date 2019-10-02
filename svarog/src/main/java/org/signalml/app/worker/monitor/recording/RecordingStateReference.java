package org.signalml.app.worker.monitor.recording;

import java.util.LinkedList;
import java.util.List;

/**
 * AtomicReference of RecordingState with added listeners.
 *
 * @author piotr.rozanski@braintech.pl
 */
class RecordingStateReference {

	private final List<RecordingListener> listeners;
	private RecordingState state;

	public RecordingStateReference() {
		listeners = new LinkedList<>();
		state = RecordingState.FINISHED;
	}

	public void addListener(RecordingListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public synchronized boolean compareAndSet(RecordingState oldState, RecordingState newState) {
		if (state == oldState) {
			set(newState);
			return true;
		} else {
			return false;
		}
	}

	public synchronized RecordingState get() {
		return state;
	}

	public synchronized void set(RecordingState newState) {
		if (newState != state) {
			state = newState;
			stateChanged(newState);
		}
	}

	private void stateChanged(RecordingState state) {
		synchronized (listeners) {
			for (RecordingListener listener : listeners) {
				listener.recordingStateChanged(state);
			}
		}
	}
}
