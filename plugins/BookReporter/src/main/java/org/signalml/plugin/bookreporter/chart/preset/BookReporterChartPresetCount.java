package org.signalml.plugin.bookreporter.chart.preset;

import java.util.Collection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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
			protected Plot getPlot() {
				XYSeries data = new XYSeries("count");

				data.add(0.0, 0.0);
				for (int i=0; i<this.counts.length; i++) {
					double secondsLeft = (i+0.1) * timeInterval;
					double secondsRight = (i+0.9) * timeInterval;
					data.add(secondsLeft/3600.0, this.counts[i]);
					data.add(secondsRight/3600.0, this.counts[i]);
				}
				data.add(signalLength/3600.0, 0.0);

				NumberAxis xAxis = new NumberAxis("time [hours]");
				xAxis.setRange(0.0, signalLength/3600.0);
				return new XYPlot(
					new XYSeriesCollection(data),
					xAxis,
					new NumberAxis(getWavesName() + " per " + getTimeInterval() + " s"),
					new XYAreaRenderer()
				);
			}
		};
	}
	
}
