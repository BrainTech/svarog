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
public class SaveVideoMsg extends BaseMessage{
	@JsonProperty("PATH")
	private String PATH;
	
	@JsonProperty("URL")
	private String URL;
	
	@JsonIgnore
	public SaveVideoMsg(String sender,String path, String url){
		super(MessageType.SAVE_VIDEO);
		setSender(sender);
		this.PATH=path;
		this.URL=url;
	}
	
}
