/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Marian Dovgialo
 */
public class SaveVideoOkMsg extends BaseMessage{
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("status")
	String getStatus(){
		return status;
	}
	
	
}
