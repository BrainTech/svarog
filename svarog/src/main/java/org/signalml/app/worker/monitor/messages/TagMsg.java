package org.signalml.app.worker.monitor.messages;

import java.util.LinkedHashMap;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Marian Dovgialo
 */
public class TagMsg extends BaseMessage{
	@JsonProperty("start_timestamp")
	private double start_timestamp;
	
	@JsonProperty("end_timestamp")
	private double end_timestamp;
	
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
	
	public double getEndTimestamp()
	{
		return end_timestamp;
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
