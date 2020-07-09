/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Marian Dovgialo
 */
public class BrokerHelloResponseMsg extends BaseMessage{
	@JsonProperty("xpub_url")
	private String xpub_url;
	
	@JsonProperty("xsub_url")
	private String xsub_url;
	
	public String getXpubUrl(){
		return xpub_url;
	}
	
	public String getXsubUrl(){
		return xsub_url;
	}
	
}
