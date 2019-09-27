package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author piotr.rozanski@braintech.pl
 */
public class SavingSignalStarting extends LauncherMessage {

	@JsonProperty("saving_session_id")
	public String savingSessionID;

}
