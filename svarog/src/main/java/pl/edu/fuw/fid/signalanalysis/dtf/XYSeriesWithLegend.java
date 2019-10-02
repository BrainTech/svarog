package pl.edu.fuw.fid.signalanalysis.dtf;

import org.jfree.data.xy.XYSeries;

/**
 * Subclass of XYSeries (from JFreeChart), including a legend (as text).
 *
 * @author ptr@mimuw.edu.pl
 */
public class XYSeriesWithLegend extends XYSeries {

	private final String legend;

	public XYSeriesWithLegend(Comparable key, String legend) {
		super(key);
		this.legend = legend;
	}

	public String getLegend() {
		return legend;
	}

}
