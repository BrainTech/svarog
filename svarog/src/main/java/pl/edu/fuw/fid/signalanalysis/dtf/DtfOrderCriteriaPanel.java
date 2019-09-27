package pl.edu.fuw.fid.signalanalysis.dtf;

import java.awt.geom.Rectangle2D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Panel displaying plot of AR criteria value vs selected model order.
 * Allows user to select feasible model order and notifies given listener object
 * about the selection.
 *
 * @author ptr@mimuw.edu.pl
 */
public class DtfOrderCriteriaPanel extends ChartPanel {

	private DtfOrderSelectionListener listener;

	private static JFreeChart createChart(final XYSeriesWithLegend[] criteria) {
		final XYSeriesCollection dataset = new XYSeriesCollection();
		for (XYSeries serie : criteria) {
			dataset.addSeries(serie);
		}
		final JFreeChart chart = ChartFactory.createXYLineChart(_("Model order selection"), _("model order"), _("criterion value"), dataset, PlotOrientation.VERTICAL, true, false, false);
		final XYPlot plot = chart.getXYPlot();
		((NumberAxis) plot.getDomainAxis()).setTickUnit(new NumberTickUnit(1.0));
		final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setLegendItemToolTipGenerator(new XYSeriesLabelGenerator() {
			@Override
			public String generateLabel(XYDataset xyd, int i) {
				return (i>=0 && i<criteria.length) ? criteria[i].getLegend() : "";
			}
		});
		for (int i=0; i<dataset.getSeriesCount(); ++i) {
			renderer.setSeriesShapesVisible(i, true);
			renderer.setSeriesShapesFilled(i, true);
		}
		return chart;
	}

	public static DtfOrderCriteriaPanel create(int maxOrder, XYSeriesWithLegend[] criteria) {
		return new DtfOrderCriteriaPanel(maxOrder, createChart(criteria));
	}

	public DtfOrderCriteriaPanel(final int maxOrder, JFreeChart chart) {
		super(chart);
		if (maxOrder <= 0) {
			throw new IllegalArgumentException("maxOrder=" + maxOrder);
		}
		final XYPlot plot = chart.getXYPlot();

		setDomainZoomable(false);
		setRangeZoomable(false);
		addChartMouseListener(new ChartMouseListener() {
			@Override
			public void chartMouseMoved(ChartMouseEvent cme) {
				// nothing here
			}

			@Override
			public void chartMouseClicked(ChartMouseEvent cme) {
				int x = cme.getTrigger().getX();
				Rectangle2D plotArea = getScreenDataArea();
				// convert position in pixels to actual value on X axis
				double value = plot.getDomainAxis().java2DToValue(x, plotArea, plot.getDomainAxisEdge());
				int selectedOrder = (int) Math.round(value);
				if (selectedOrder > 0 && selectedOrder <= maxOrder) {
					// display selected model order
					plot.clearDomainMarkers();
					plot.addDomainMarker(new ValueMarker(selectedOrder));
					// user-defined callback on model order change
					DtfOrderSelectionListener call = listener;
					if (call != null) {
						call.modelOrderSelected(selectedOrder);
					}
				}
			}
		});
	}

	void setListener(DtfOrderSelectionListener listener) {
		this.listener = listener;
	}

}
