package org.signalml.plugin.bookreporter.chart.preset;

import org.signalml.plugin.bookreporter.chart.BookReporterChartData;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterChartPresetPercentage extends BookReporterChartPresetPerInterval {

	@Override
	public String getCaption() {
		return "<html>time % of<br>" + getWavesName() + "<br>per " + getTimeInterval() + " s</html>";
	}

	@Override
	public BookReporterChartData createEmptyData(double signalLength) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
