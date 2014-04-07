package org.signalml.plugin.bookreporter.data;

import java.io.Serializable;

/**
 * @author piotr@develancer.pl
 * (based on Michal Dobaczewski's NewStagerResult)
 */
public class BookReporterResult implements Serializable {

	private final int chartCount;

	public BookReporterResult(int chartCount) {
		this.chartCount = chartCount;
	}
	
	public int getChartCount() {
		return this.chartCount;
	}
}
