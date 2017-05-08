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
public class SaveVideoDoneMsg extends BaseMessage{
	@JsonProperty("ts")
	private double ts;
	
	@JsonProperty("status")
	private String status;
	
	double getTs(){
		return ts;
	}
	
	@JsonProperty("status")
	String getStatus(){
		return status;
	}
	
}
