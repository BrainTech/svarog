package org.signalml.plugin.bookreporter.chart.preset;

import java.util.Collection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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
			protected Plot getPlot() {
				XYSeries data = new XYSeries("count");
				int dataPointCount = (int) Math.ceil(signalLength/timeInterval);
				
				data.add(0.0, 0.0);
				for (int i=0; i<dataPointCount; i++) {
					double seconds = i * timeInterval;
					BookReporterTimeInterval interval = BookReporterTimeInterval.create(seconds, seconds+timeInterval);
					double percentage = 100.0 * intervals.cover(interval) / timeInterval;
					double secondsLeft = (i+0.1) * timeInterval;
					double secondsRight = (i+0.9) * timeInterval;
					data.add(secondsLeft/3600.0, percentage);
					data.add(secondsRight/3600.0, percentage);
				}
				data.add(signalLength/3600.0, 0.0);

				NumberAxis xAxis = new NumberAxis("time [hours]");
				xAxis.setRange(0.0, signalLength/3600.0);
				return new XYPlot(
					new XYSeriesCollection(data),
					xAxis,
					new NumberAxis("% of each " + getTimeInterval() + " s occupied by " + getWavesName()),
					new XYAreaRenderer()
				);
			}
		};
	}

}
