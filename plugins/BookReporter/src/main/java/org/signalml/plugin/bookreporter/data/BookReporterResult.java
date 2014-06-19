package org.signalml.plugin.bookreporter.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import org.jfree.chart.JFreeChart;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagStyles;
import org.signalml.plugin.export.signal.Tag;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterResult implements Serializable {

	private final List<JFreeChart> charts;
	
	private final TagStyles tagStyles;
	
	private final TreeSet<Tag> tags;
	
	public BookReporterResult(TagStyles tagStyles) {
		this.charts = new LinkedList<JFreeChart>();
		this.tagStyles = tagStyles;
		this.tags = new TreeSet<Tag>();
	}
	
	public void addChart(JFreeChart chart) {
		this.charts.add(chart);
	}
	
	public void addTags(Collection<? extends Tag> tags) {
		this.tags.addAll(tags);
	}
	
	public List<JFreeChart> getCharts() {
		return Collections.unmodifiableList(charts);
	}

	public StyledTagSet getTags() {
		return new StyledTagSet(tagStyles, tags);
	}

	public int getChartCount() {
		return this.charts.size();
	}
}
