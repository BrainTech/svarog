package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonProperty;

public class LauncherMessage extends BaseMessage{
	@JsonProperty("sender_ip")
	private String senderIp = "";
	
	@JsonProperty("receiver")
	private String receiver ="";

	public LauncherMessage(MessageType type) {
		super(type);
	}
	
	public LauncherMessage(){}
	
	public String getSenderIp() {
		return senderIp;
	}

	public void setSenderIp(String senderIp) {
		this.senderIp = senderIp;
	}
	
	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

}

