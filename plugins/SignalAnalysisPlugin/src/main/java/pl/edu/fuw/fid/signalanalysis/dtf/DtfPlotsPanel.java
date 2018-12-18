package pl.edu.fuw.fid.signalanalysis.dtf;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import static org.signalml.plugin.i18n.PluginI18n._;

/**
 * Panel displaying precomputed spectral features of the AR model,
 * all at once (in rectangular grid), or one at a time.
 *
 * @author ptr@mimuw.edu.pl
 */
public class DtfPlotsPanel extends JPanel {

	private final int dimension;
	private final ChartPanel[] charts;
	private final boolean[] normalizable;

	// if >= 0, only one chart is visible
	private int chartSelected = -1;

	public DtfPlotsPanel(XYSeries[][] series) {
		super(new BorderLayout());
		dimension = series.length;
		charts = new ChartPanel[dimension * dimension];
		normalizable = new boolean[dimension * dimension];

		// add placeholder which shall be visible until the user selects model order
		add(new JLabel(_("select AR model order to display graphs"), SwingConstants.CENTER), BorderLayout.CENTER);

		// prepare charts to be displayed
		int index = 0;
		for (int i=0; i<series.length; ++i) {
			if (series[i].length != dimension) {
				throw new RuntimeException(_("series[][] is not a square array"));
			}
			for (int j=0; j<series.length; ++j) {
				XYSeries serie = series[i][j];
				if (serie != null) {
					final int indexFinal = index;
					JFreeChart chart = ChartFactory.createXYAreaChart(serie.getDescription(), _("frequency [Hz]"), null, new XYSeriesCollection(serie), PlotOrientation.VERTICAL, false, false, false);
					ChartPanel panel = new ChartPanel(chart);
					panel.setDomainZoomable(false);
					panel.setRangeZoomable(false);
					panel.addChartMouseListener(new ChartMouseListener() {
						@Override
						public void chartMouseMoved(ChartMouseEvent cme) {
							// nothing here
						}
						@Override
						public void chartMouseClicked(ChartMouseEvent cme) {
							if (chartSelected == indexFinal) {
								showAllCharts();
							} else {
								showSingleChart(indexFinal);
							}
						}
					});
					charts[index] = panel;
					normalizable[index] = (i != j);
				}
				index++;
			}
		}
	}

	public void rescaleCharts() {
		double max = 0.0;
		for (int i=0; i<charts.length; ++i) {
			if (normalizable[i]) {
				XYDataset data = charts[i].getChart().getXYPlot().getDataset();
				int length = data.getItemCount(0);
				for (int f=0; f<length; ++f) {
					max = Math.max(max, data.getYValue(0, f));
				}
			}
		}
		for (int i=0; i<charts.length; ++i) {
			if (normalizable[i]) {
				charts[i].getChart().getXYPlot().getRangeAxis().setRange(0, max);
			}
		}
	}

	public void showAllCharts() {
		this.removeAll();
		chartSelected = -1;
		setLayout(new GridLayout(dimension, dimension));
		for (int i=0; i<charts.length; ++i) {
			if (charts[i] != null) {
				this.add(charts[i], i);
			}
		}
		revalidate();
	}

	public void showSingleChart(int index) {
		this.removeAll();
		chartSelected = index;
		setLayout(new BorderLayout());
		this.add(charts[index], BorderLayout.CENTER);
		revalidate();
	}

}
