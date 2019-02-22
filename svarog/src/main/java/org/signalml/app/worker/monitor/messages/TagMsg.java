package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Marian Dovgialo
 */
public class TagMsg extends IncompleteTagMsg {

	@JsonProperty("end_timestamp")
	private double end_timestamp;

	/**
	 * @return tag duration in seconds
	 */
	@Override
	public double getDuration()
	{
		return getEndTimestamp() - getStartTimestamp();
	}

	public double getEndTimestamp()
	{
		return end_timestamp;
	}
}
