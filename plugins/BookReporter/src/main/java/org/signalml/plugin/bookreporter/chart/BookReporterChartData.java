package org.signalml.plugin.bookreporter.chart;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.signalml.plugin.bookreporter.data.BookReporterConstants;
import org.signalml.plugin.bookreporter.data.BookReporterFASPThreshold;
import org.signalml.plugin.bookreporter.data.book.BookReporterAtom;
import org.signalml.plugin.bookreporter.logic.filter.BookReporterBookAtomFilterBase;
import org.signalml.plugin.bookreporter.logic.filter.IBookReporterBookAtomFilter;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * @author piotr@develancer.pl
 */
public abstract class BookReporterChartData {

	private final IBookReporterBookAtomFilter filter;
	private final LinkedList<Tag> tags;
	private final TagStyle tagStyle;
	
	public BookReporterChartData(BookReporterFASPThreshold threshold) {
		this(threshold, null);
	}

	public BookReporterChartData(BookReporterFASPThreshold threshold, TagStyle tagStyle) {
		this.filter = new BookReporterBookAtomFilterBase(threshold);
		this.tags = new LinkedList<Tag>();
		this.tagStyle = tagStyle;
	}
	
	protected abstract void include(Collection<BookReporterAtom> filteredAtoms);
	
	protected void includeTags(Collection<BookReporterAtom> filteredAtoms) {
		if (this.tagStyle != null) {
			for (BookReporterAtom atom : filteredAtoms) {
				double halfWidth = atom.scale * BookReporterConstants.TIME_OCCUPATION_SCALE;
				Tag tag = new Tag(tagStyle, atom.position - halfWidth, 2 * halfWidth);
				tags.add(tag);
			}
		}
	}

	protected abstract Plot getPlot();

	public JFreeChart getChart() {
		JFreeChart chart = new JFreeChart(this.getPlot());
		chart.removeLegend();
		return chart;
	}
	
	public List<Tag> getTagList() {
		return Collections.unmodifiableList(this.tags);
	}

	public void process(Collection<BookReporterAtom> allAtoms) {
		BookReporterAtom[] atomArray = (BookReporterAtom[]) allAtoms.toArray(
			new BookReporterAtom[allAtoms.size()]
		);
		Collection<BookReporterAtom> filteredAtoms = this.filter.filter(atomArray);
		this.include(filteredAtoms);
		this.includeTags(filteredAtoms);
	}

}
