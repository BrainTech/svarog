package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author piotr.rozanski@braintech.pl
 */
public class CheckSavingSignalStatus extends LauncherMessage {

	@JsonProperty("saving_session_id")
	public String savingSessionID;

	public CheckSavingSignalStatus(String savingSessionID) {
		super(MessageType.CHECK_SAVING_SIGNAL_STATUS);
		this.savingSessionID = savingSessionID;
	}
}
