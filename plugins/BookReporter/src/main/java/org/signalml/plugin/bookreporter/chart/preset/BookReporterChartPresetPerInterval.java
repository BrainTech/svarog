package org.signalml.plugin.bookreporter.chart.preset;

/**
 * @author piotr@develancer.pl
 */
public abstract class BookReporterChartPresetPerInterval extends BookReporterChartPreset {
	
	public static final int DEFAULT_TIME_INTERVAL = 180;

	private int timeInterval;

	public BookReporterChartPresetPerInterval() {
		this.timeInterval = DEFAULT_TIME_INTERVAL;
	}
	
	public int getTimeInterval() {
		return this.timeInterval;
	}
	
	public void setTimeInterval(int timeInterval) {
		this.timeInterval = timeInterval;
	}

}
