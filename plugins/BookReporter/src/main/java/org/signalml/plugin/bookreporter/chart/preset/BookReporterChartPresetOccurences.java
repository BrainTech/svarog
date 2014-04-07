package org.signalml.plugin.bookreporter.chart.preset;

import org.signalml.plugin.bookreporter.chart.BookReporterChartData;

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
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
