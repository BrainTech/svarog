package org.signalml.app.worker.monitor.messages;

import java.util.LinkedHashMap;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author piotr.rozanski@braintech.pl
 */
public class IncompleteTagMsg extends BaseMessage {

	@JsonProperty("start_timestamp")
	private double start_timestamp;

	@JsonProperty("id")
	private String id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("channels")
	private String channels;

	@JsonProperty("desc")
	private LinkedHashMap<String, Object> desc;

	public double getStartTimestamp()
	{
		return start_timestamp;
	}

	/**
	 * @return tag duration in seconds (∞ for incomplete tags)
	 */
	public double getDuration()
	{
		// special value to mark unfinished tags
		return Double.POSITIVE_INFINITY;
	}

	public String getID()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getChannels()
	{
		return channels;
	}

	public LinkedHashMap<String, Object> getDescription()
	{
		return desc;
	}
}
