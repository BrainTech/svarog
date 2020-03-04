package pl.edu.fuw.fid.signalanalysis;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * Subclass of JFreeChart's ChartPanel with all mouse interaction disabled.
 *
 * @author ptr@mimuw.edu.pl
 */
public class NonInteractiveChartPanel extends ChartPanel {

	public NonInteractiveChartPanel(JFreeChart chart) {
		super(chart);
		setMouseZoomable(false);
		setPopupMenu(null);
	}
}
