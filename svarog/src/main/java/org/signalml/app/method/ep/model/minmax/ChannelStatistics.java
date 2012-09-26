package org.signalml.app.method.ep.model.minmax;

/**
 * Contains the statistics for a single channel - its min/max signal value and time
 * when this min/max occured (in seconds).
 *
 * @author Piotr Szachewicz
 */
public class ChannelStatistics {

	private String channelName;
	private double minTime;
	private double minValue;
	private double maxTime;
	private double maxValue;

	public ChannelStatistics(String channelName, double minTime, double minValue, double maxTime, double maxValue) {
		this.channelName = channelName;
		this.minTime = minTime;
		this.minValue = minValue;
		this.maxTime = maxTime;
		this.maxValue = maxValue;
	}

	public String getChannelName() {
		return channelName;
	}

	public double getMinTime() {
		return minTime;
	}

	public double getMinValue() {
		return minValue;
	}

	public double getMaxTime() {
		return maxTime;
	}

	public double getMaxValue() {
		return maxValue;
	}

}
