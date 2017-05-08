package org.signalml.app.worker.monitor.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;

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

