package pl.edu.fuw.fid.signalanalysis.dtf;

import org.jfree.data.xy.XYSeries;

/**
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
