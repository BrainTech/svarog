package org.signalml.plugin.bookreporter.chart;

import java.util.Collection;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.signalml.plugin.bookreporter.data.BookReporterFASPThreshold;
import org.signalml.plugin.bookreporter.data.book.BookReporterAtom;
import org.signalml.plugin.bookreporter.logic.book.tag.helper.BookReporterBookAtomFilterBase;
import org.signalml.plugin.bookreporter.logic.book.tag.helper.IBookReporterBookAtomFilter;

/**
 * @author piotr@develancer.pl
 */
public abstract class BookReporterChartData {

	private final IBookReporterBookAtomFilter filter;
	
	public BookReporterChartData(BookReporterFASPThreshold threshold) {
		this.filter = new BookReporterBookAtomFilterBase(threshold);
	}
	
	protected abstract void include(Collection<BookReporterAtom> filteredAtoms);

	protected abstract Plot getPlot();

	public JFreeChart getChart() {
		JFreeChart chart = new JFreeChart(this.getPlot());
		chart.removeLegend();
		return chart;
	}
	
	public void process(Collection<BookReporterAtom> allAtoms) {
		BookReporterAtom[] atomArray = (BookReporterAtom[]) allAtoms.toArray(
			new BookReporterAtom[allAtoms.size()]
		);
		Collection<BookReporterAtom> filteredAtoms = this.filter.filter(atomArray);
		this.include(filteredAtoms);
	}

}
