package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Marian Dovgialo
 */
public class TagMsg extends IncompleteTagMsg {

	@JsonProperty("end_timestamp")
	private double end_timestamp;

	public TagMsg() {
		super();
	}

	public TagMsg(String id, String name, String channels, double start_timestamp, double end_timestamp) {
		super(MessageType.TAG_MSG, id, name, channels, start_timestamp);
		this.end_timestamp = end_timestamp;
	}

	/**
	 * @return tag duration in seconds
	 */
	@JsonIgnore
	@Override
	public double getDuration()
	{
		return getEndTimestamp() - getStartTimestamp();
	}

	@JsonIgnore
	public double getEndTimestamp()
	{
		return end_timestamp;
	}
}
