package org.signalml.plugin.bookreporter.chart.preset;

import java.util.Collection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.signalml.plugin.bookreporter.chart.BookReporterChartData;
import org.signalml.plugin.bookreporter.data.BookReporterConstants;
import org.signalml.plugin.bookreporter.data.book.BookReporterAtom;
import org.signalml.plugin.bookreporter.logic.intervals.BookReporterTimeInterval;
import org.signalml.plugin.bookreporter.logic.intervals.BookReporterTimeIntervalSet;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterChartPresetPercentage extends BookReporterChartPresetPerInterval {

	@Override
	public String getCaption() {
		return "<html>time % of<br>" + getWavesName() + "<br>per " + getTimeInterval() + " s</html>";
	}

	@Override
	public BookReporterChartData createEmptyData(final double signalLength, TagStyle tagStyle) {
		return new BookReporterChartData(getThreshold(), tagStyle) {

			private final int timeInterval = getTimeInterval();
			private final BookReporterTimeIntervalSet intervals = new BookReporterTimeIntervalSet();

			@Override
			protected void include(Collection<BookReporterAtom> filteredAtoms) {
				for (BookReporterAtom atom : filteredAtoms) {
					BookReporterTimeInterval newInterval = BookReporterTimeInterval.create(
						atom.position - atom.scale * BookReporterConstants.TIME_OCCUPATION_SCALE,
						atom.position + atom.scale * BookReporterConstants.TIME_OCCUPATION_SCALE
					);
					intervals.add(newInterval);
				}
			}

			@Override
			public XYPlot getPlot() {
				XYIntervalSeries data = new XYIntervalSeries("% occupied in "+getTimeInterval()+" s");
				int dataPointCount = (int) Math.ceil(signalLength/timeInterval);
				for (int i=0; i<dataPointCount; i++) {
					double secondsLeft = i * timeInterval;
					double secondsCenter = (i+0.5) * timeInterval;
					double secondsRight = (i+1) * timeInterval;
					BookReporterTimeInterval interval = BookReporterTimeInterval.create(secondsLeft, secondsRight);
					double percentage = 100.0 * intervals.cover(interval) / timeInterval;
					data.add(secondsCenter/3600.0, secondsLeft/3600.0, secondsRight/3600.0, percentage, percentage, percentage);
				}

				NumberAxis yAxis = new NumberAxis(getWavesName());
				XYIntervalSeriesCollection collection = new XYIntervalSeriesCollection();
				collection.addSeries(data);
				return new XYPlot(
					collection,
					new NumberAxis(), yAxis,
					new XYBarRenderer()
				);
			}
		};
	}

}
