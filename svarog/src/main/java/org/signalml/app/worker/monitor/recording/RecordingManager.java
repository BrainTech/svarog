package org.signalml.app.worker.monitor.recording;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.apache.log4j.Logger;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.worker.monitor.Helper;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.FinishSavingSignal;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.SavingSignalError;
import org.signalml.app.worker.monitor.messages.SavingSignalStarting;
import org.signalml.app.worker.monitor.messages.StartSavingSignal;

/**
 * @author piotr.rozanski@braintech.pl
 */
public class RecordingManager {

	private static final Logger logger = Logger.getLogger(RecordingManager.class);

	private final RecordingStateReference state;
	private String savingSessionID;

	public RecordingManager() {
		state = new RecordingStateReference();
	}

	public void addRecordingListener(RecordingListener listener) {
		state.addListener(listener);
	}

	public boolean startRecording(StartSavingSignal request) {
		if (state.compareAndSet(RecordingState.FINISHED, RecordingState.INITIALIZATION)) {
			String errorMessage = _("Failed to start recording");
			try {
				BaseMessage response = Helper.sendRequestAndParseResponse(
					request,
					Helper.getOpenBCIIpAddress(), Helper.getOpenbciPort(),
					null
				);
				if (response instanceof SavingSignalStarting) {
					savingSessionID = ((SavingSignalStarting) response).savingSessionID;
					RecordingStateChecker checker = new RecordingStateChecker(savingSessionID, state);
					checker.start();
					return true;
				} else if (response instanceof SavingSignalError) {
					errorMessage += ": " + ((SavingSignalError) response).details.toString();
				} else {
					errorMessage += ": received incompatible message type " + response.getClass().getSimpleName();
				}
			} catch (OpenbciCommunicationException ex) {
				logger.error("cannot send recording request", ex);
				errorMessage = ": " + ex.getMessage();
			}
			// restoring the original state
			state.set(RecordingState.FINISHED);
			Dialogs.showError(errorMessage);
		}
		return false;
	}

	public boolean stopRecording() {
		if (state.compareAndSet(RecordingState.SAVING, RecordingState.FINISHING)) {
			FinishSavingSignal finishMessage = new FinishSavingSignal(savingSessionID);

			try {
				Helper.sendRequestAndParseResponse(
					finishMessage,
					Helper.getOpenBCIIpAddress(), Helper.getOpenbciPort(),
					MessageType.SAVING_SIGNAL_FINISHING
				);
				return true;
			} catch (OpenbciCommunicationException ex) {
				logger.error("cannot send stop recording request", ex);
				// restoring the original state
				state.set(RecordingState.SAVING);
			}
		}
		return false;
	}

	public RecordingState getRecordingState() {
		return state.get();
	}
}
