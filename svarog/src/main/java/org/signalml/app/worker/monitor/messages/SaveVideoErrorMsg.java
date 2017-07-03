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
public class SaveVideoErrorMsg extends BaseMessage{
	@JsonProperty("details")
	private String details;
	
	@JsonProperty("status")
	private String status;
	
	String getDetails(){
		return details;
	}
	
	@JsonProperty("status")
	String getStatus(){
		return status;
	}
	
}
