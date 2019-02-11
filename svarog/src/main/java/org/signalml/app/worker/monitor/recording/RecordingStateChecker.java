package org.signalml.app.worker.monitor.recording;

import org.apache.log4j.Logger;
import org.signalml.app.worker.monitor.Helper;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.CheckSavingSignalStatus;
import org.signalml.app.worker.monitor.messages.SavingSignalError;
import org.signalml.app.worker.monitor.messages.SavingSignalStatus;

/**
 * Thread for periodic checking of saving status, for given session ID.
 *
 * @author piotr.rozanski@braintech.pl
 */
class RecordingStateChecker extends Thread {

	private static final Logger logger = Logger.getLogger(RecordingStateChecker.class);

	private final String savingSessionID;
	private final RecordingStateReference state;

	public RecordingStateChecker(String savingSessionID, RecordingStateReference state) {
		super(savingSessionID);
		this.savingSessionID = savingSessionID;
		this.state = state;
		setDaemon(true);
	}

	@Override
	public void run() {
		final CheckSavingSignalStatus checkMessage = new CheckSavingSignalStatus(savingSessionID);
		RecordingState lastState = null;
		while (RecordingState.FINISHED != lastState) {
			try {
				sleep(500);
			} catch (InterruptedException ex) {
				// does not matter
			}
			try {
				BaseMessage response = Helper.sendRequestAndParseResponse(
					checkMessage,
					Helper.getOpenBCIIpAddress(), Helper.getOpenbciPort(),
					null // any type
				);
				if (response instanceof SavingSignalStatus) {
					SavingSignalStatus status = (SavingSignalStatus) response;
					lastState = RecordingState.valueOf(status.status.toUpperCase());
				} else if (response instanceof SavingSignalError) {
					SavingSignalError error = (SavingSignalError) response;
					logger.error("recording finished with error: " + error.details);
					lastState = RecordingState.FINISHED;
				} else {
					logger.warn("received unexpected response while checking recording status");
					continue;
				}

				state.set(lastState);
				if (lastState == RecordingState.FINISHED) {
					break;
				}
			} catch (OpenbciCommunicationException ex) {
				// communication error, will try again
			}
		}
	}
}
