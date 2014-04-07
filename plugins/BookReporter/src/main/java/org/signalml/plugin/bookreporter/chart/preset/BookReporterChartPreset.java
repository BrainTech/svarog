package org.signalml.plugin.bookreporter.chart.preset;

import org.signalml.plugin.bookreporter.chart.BookReporterChartData;
import org.signalml.plugin.bookreporter.data.BookReporterFASPThreshold;

/**
 * @author piotr@develancer.pl
 */
public abstract class BookReporterChartPreset {

	private BookReporterFASPThreshold threshold;
	private String wavesName;

	public BookReporterChartPreset() {
		this.threshold = BookReporterFASPThreshold.UNLIMITED;
		this.wavesName = "";
	}

	public abstract String getCaption();
	
	public BookReporterFASPThreshold getThreshold() {
		return this.threshold;
	}
	
	public void setThreshold(BookReporterFASPThreshold threshold) {
		this.threshold = threshold;
	}

	public String getWavesName() {
		return this.wavesName;
	}
	
	public void setWavesName(String wavesName) {
		this.wavesName = wavesName;
	}
	
	public abstract BookReporterChartData createEmptyData(double signalLength);
	
}
