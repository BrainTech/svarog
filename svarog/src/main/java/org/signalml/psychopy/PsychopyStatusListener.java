package org.signalml.psychopy;

import org.signalml.app.worker.monitor.messages.BaseMessage;

/**
 * Simple listener notified about the status of the psychopy experiment.
 *
 * @author maciej.pawlisz@braintech.pl
 */
public interface PsychopyStatusListener {

	/**
	 * Called whenever the psychopy experiment status may have changed.
	 * There is no guarantee that the new status actually differs from a previous one.
	 *
	 * @param status_message  current (new) status of the recording
	 */
	public void psychopyStatusChanged(BaseMessage status_message);

}
