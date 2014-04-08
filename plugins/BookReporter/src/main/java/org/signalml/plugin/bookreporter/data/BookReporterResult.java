package org.signalml.plugin.bookreporter.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.jfree.chart.JFreeChart;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterResult implements Serializable {

	private final List<JFreeChart> charts;

	public BookReporterResult() {
		this.charts = new LinkedList<JFreeChart>();
	}
	
	public void addChart(JFreeChart chart) {
		this.charts.add(chart);
	}
	
	public List<JFreeChart> getCharts() {
		return Collections.unmodifiableList(charts);
	}
	
	public int getChartCount() {
		return this.charts.size();
	}
}
