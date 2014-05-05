package org.signalml.plugin.bookreporter.chart.preset;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import org.signalml.plugin.bookreporter.chart.BookReporterChartData;
import org.signalml.plugin.bookreporter.data.book.BookReporterAtom;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterChartPresetOccurences extends BookReporterChartPreset {

	@Override
	public String getCaption() {
		return "<html>occurences of<br>" + getWavesName() + "</html>";
	}

	@Override
	public BookReporterChartData createEmptyData(double signalLength) {
		final double signalHours = signalLength / 3600.0;
		return new BookReporterChartData(getThreshold()) {

			private final LinkedList<Double> positions = new LinkedList<Double>();

			@Override
			protected void include(Collection<BookReporterAtom> filteredAtoms) {
				for (BookReporterAtom atom : filteredAtoms) {
					positions.add(atom.position / 3600.0);
				}
			}

			@Override
			protected Plot getPlot() {
				NumberAxis xAxis = new NumberAxis("time [hours]");
				NumberAxis yAxis = new NumberAxis("occurences of " + getWavesName());
				xAxis.setRange(0.0, signalHours);
				yAxis.setRange(0.0, 1.0);
				yAxis.setTickUnit(new NumberTickUnit(1.0));
				XYPlot plot = new XYPlot(new XYSeriesCollection(), xAxis, yAxis, new XYAreaRenderer());
				for (double time : positions) {
					ValueMarker marker = new ValueMarker(time);
					plot.addDomainMarker(marker);
				}
				return plot;
			}
		};
	}

}
