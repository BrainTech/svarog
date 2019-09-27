package org.signalml.app.worker.monitor.messages;

import java.util.LinkedHashMap;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author piotr.rozanski@braintech.pl
 */
public class SavingSignalError extends LauncherMessage {

	@JsonProperty("saving_session_id")
	public String savingSessionID;

	@JsonProperty("details")
	public LinkedHashMap<String, Object> details;

}
