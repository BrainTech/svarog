package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author piotr.rozanski@braintech.pl
 */
public class FinishSavingSignal extends LauncherMessage {

	@JsonProperty("saving_session_id")
	public String savingSessionID;

	@JsonIgnore
	public FinishSavingSignal(String savingSessionID) {
		super(MessageType.FINISH_SAVING_SIGNAL);
		this.savingSessionID = savingSessionID;
	}
}
