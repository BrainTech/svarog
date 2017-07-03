package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Marian Dovgialo
 */
public class BrokerHelloMsg extends BaseMessage{
	@JsonProperty("peer_url")
	private String peer_url;
	
	@JsonProperty("broker_url")
	private String broker_url;
	
	@JsonIgnore
	public BrokerHelloMsg(String peer_url, String broker_url){
		super(MessageType.BROKER_HELLO);
		this.peer_url=peer_url;
		this.broker_url=broker_url;
	}
	
}
