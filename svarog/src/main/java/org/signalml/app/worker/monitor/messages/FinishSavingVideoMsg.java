package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Marian Dovgialo
 */
public class FinishSavingVideoMsg extends BaseMessage{
	
	@JsonIgnore
	public FinishSavingVideoMsg(String sender){
		super(MessageType.FINISH_SAVING_VIDEO);
		this.setSender(sender);
	}
	
}
