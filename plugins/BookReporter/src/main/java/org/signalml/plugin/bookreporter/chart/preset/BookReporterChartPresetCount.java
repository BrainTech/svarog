package org.signalml.plugin.bookreporter.chart.preset;

import java.util.Collection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.signalml.plugin.bookreporter.chart.BookReporterChartData;
import org.signalml.plugin.bookreporter.data.book.BookReporterAtom;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterChartPresetCount extends BookReporterChartPresetPerInterval {
	
	@Override
	public String getCaption() {
		return "<html>count of<br>" + getWavesName() + "<br>per " + getTimeInterval() + " s</html>";
	}

	@Override
	public BookReporterChartData createEmptyData(final double signalLength, TagStyle tagStyle) {
		return new BookReporterChartData(getThreshold(), tagStyle) {

			private final int timeInterval = getTimeInterval();
			private final int[] counts = new int[ (int) Math.ceil(signalLength/timeInterval) ];

			@Override
			protected void include(Collection<BookReporterAtom> filteredAtoms) {
				for (BookReporterAtom atom : filteredAtoms) {
					int interval_index = (int) Math.floor(atom.position / this.timeInterval);
					if (interval_index>=0 && interval_index<this.counts.length) {
						this.counts[interval_index]++;
					}
				}
			}

			@Override
			public XYPlot getPlot() {
				XYIntervalSeries data = new XYIntervalSeries("count");
				for (int i=0; i<this.counts.length; i++) {
					double secondsLeft = i * timeInterval;
					double secondsCenter = (i+0.5) * timeInterval;
					double secondsRight = (i+1) * timeInterval;
					data.add(secondsCenter/3600.0, secondsLeft/3600.0, secondsRight/3600.0, counts[i], counts[i], counts[i]);
				}

				NumberAxis yAxis = new NumberAxis(getWavesName() + " per " + getTimeInterval() + " s");
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
