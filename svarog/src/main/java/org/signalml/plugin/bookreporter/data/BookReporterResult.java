package org.signalml.plugin.bookreporter.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagStyles;
import org.signalml.plugin.export.signal.Tag;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterResult implements Serializable {

	private final List<XYPlot> plots;
	
	private final TagStyles tagStyles;
	
	private final TreeSet<Tag> tags;
	
	private ValueAxis timeAxis;
	
	public BookReporterResult(TagStyles tagStyles) {
		this.plots = new LinkedList<>();
		this.tagStyles = tagStyles;
		this.tags = new TreeSet<>();
		this.timeAxis = null;
	}
	
	public void addPlot(XYPlot plot) {
		this.plots.add(plot);
	}
	
	public void addTags(Collection<? extends Tag> tags) {
		this.tags.addAll(tags);
	}
	
	public int getPlotCount() {
		return this.plots.size();
	}
	
	public List<XYPlot> getPlots() {
		return Collections.unmodifiableList(plots);
	}

	public StyledTagSet getTags() {
		return new StyledTagSet(tagStyles, tags);
	}
	
	public ValueAxis getTimeAxis() {
		return this.timeAxis;
	}

	public void setTimeAxis(ValueAxis timeAxis) {
		this.timeAxis = timeAxis;
	}
}
