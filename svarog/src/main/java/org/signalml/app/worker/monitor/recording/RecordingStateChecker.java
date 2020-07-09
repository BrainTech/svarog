package org.signalml.app.worker.monitor.recording;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
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
					logger.error(_("recording finished with error: ") + error.details);
                                        String error_text = "";
                                        for (Object err_text: error.details.values())
                                                {
                                                   try{
                                                       error_text += (String)err_text;
                                                   }
                                                   catch(ClassCastException e)
                                                   {
                                                       error_text += err_text.toString();
                                                   }
                                                   error_text += "\n";
                                                }
                                        error_text += _("Signal preceeding this error message is not lost.") + "\n";
                                        String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                                        error_text += _("Error received at ") + timeStamp;

                                        Dialogs.showError(_("Signal saving error"), error_text);
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
