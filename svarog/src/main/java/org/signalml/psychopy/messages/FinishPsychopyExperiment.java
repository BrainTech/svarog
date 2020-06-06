package org.signalml.psychopy.messages;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.signalml.app.worker.monitor.messages.*;

/**
 *
 * @author Maciej Pawlisz
 */
public class FinishPsychopyExperiment extends BaseMessage{
	
	@JsonIgnore
	public FinishPsychopyExperiment(String sender){
		super(MessageType.FINISH_PSYCHOPY_EXPERIMENT);
		this.setSender(sender);
	}
	
}
