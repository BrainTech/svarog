package org.signalml.plugin.bookreporter.chart.preset;

import java.util.Collection;
import java.util.LinkedList;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.plugin.bookreporter.chart.BookReporterChartData;
import org.signalml.plugin.bookreporter.data.BookReporterConstants;
import org.signalml.plugin.bookreporter.data.book.BookReporterAtom;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterChartPresetOccurences extends BookReporterChartPreset {

	private static class Occurrence {
		public final double time;
		public final double value;
		public final double length;

		public Occurrence(double time, double value, double length) {
			this.time = time;
			this.value = value;
			this.length = length;
		}
	}

	@Override
	public String getCaption() {
		return "<html>" + String.format(_("occurences of<br>%s"), getWavesName()) + "</html>";
	}

	@Override
	public BookReporterChartData createEmptyData(final double signalLength, TagStyle tagStyle) {
		return new BookReporterChartData(getThreshold(), tagStyle) {

			private final LinkedList<Occurrence> occurrences = new LinkedList<>();

			@Override
			protected void include(Collection<BookReporterAtom> filteredAtoms) {
				for (BookReporterAtom atom : filteredAtoms) {
					double time = atom.position / 3600.0;
					double length = atom.scale * BookReporterConstants.TIME_OCCUPATION_SCALE / 1800.0;
					occurrences.add(new Occurrence(time, atom.amplitude, length));
				}
			}

			@Override
			public XYPlot getPlot() {
				XYIntervalSeries data = new XYIntervalSeries(_("amplitudes [µV]"));
				for (Occurrence o : occurrences) {
					data.add(o.time, o.time-0.5*o.length, o.time+0.5*o.length, o.value, o.value, o.value);
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
