package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author piotr.rozanski@braintech.pl
 */
public class SavingSignalStatus extends LauncherMessage {

	@JsonProperty("saving_session_id")
	public String savingSessionID;

	@JsonProperty("status")
	public String status;

}
