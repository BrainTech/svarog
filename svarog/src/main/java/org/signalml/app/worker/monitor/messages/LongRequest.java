/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author marian
 */
public class LongRequest extends LauncherMessage{
	
	@JsonProperty("client_push_address")
	protected String clientPushAddress;
	
	public LongRequest(MessageType type) {
		super(type);
		clientPushAddress = "";
	}
	
	public String getClientPushAddress() {
		return clientPushAddress;
	}

	public void setClientPushAddress(String clientPushAddress) {
		this.clientPushAddress = clientPushAddress;
	}
}
