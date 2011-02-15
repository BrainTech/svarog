/* FilterResponseChartPanel.java created 2011-02-06
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RectangleInsets;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public abstract class ResponseChartPanel extends ChartPanel {

	protected final MessageSourceAccessor messageSource;

	public ResponseChartPanel(MessageSourceAccessor messageSource) {
		super(createChart());
		this.messageSource = messageSource;

		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(450, 150));

		getPlot().setRangeAxis(createRangeAxis());
		getPlot().setDomainAxis(createDomainAxis());

		getPlot().getDomainAxis().setLabel(getDomainAxisName());

		setDomainZoomable(false);
		setRangeZoomable(false);
		setMouseZoomable(false);
		setPopupMenu(null);
	}

	public void setTitle(String titleText) {
		TextTitle title = new TextTitle(titleText, createDefaultFont());
		getChart().setTitle(title);
	}

	protected static JFreeChart createChart() {
		JFreeChart newChart = new JFreeChart(createPlot());

		newChart.setBorderVisible(true);
		newChart.setBackgroundPaint(Color.WHITE);
		newChart.setPadding(new RectangleInsets(5, 5, 5, 5));
		newChart.removeLegend();

		return newChart;
	}

	protected static XYPlot createPlot() {
		XYPlot newPlot = new XYPlot();

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		newPlot.setRenderer(renderer);

		return newPlot;
	}

	protected NumberAxis createDomainAxis() {
		NumberAxis domainAxis = new NumberAxis();
		domainAxis.setAutoRange(false);
		//domainAxis.setLabel(messageSource.getMessage("editSampleFilter.graphFrequencyLabel"));
		return domainAxis;
	}

	protected boolean isDomainAxisLogarithmic() {
		return false;
	}

	protected Font createDefaultFont() {
		return new Font(Font.DIALOG, Font.PLAIN, 12);
	}

	protected XYPlot getPlot() {
		return getChart().getXYPlot();
	}

	protected abstract NumberAxis createRangeAxis();

	protected NumberAxis createLogarithmicAxis(double minimum, double maximum) {
		NumberAxis axis = new LogarithmicAxis("");
		axis.setAutoRange(false);
		((LogarithmicAxis) axis).setStrictValuesFlag(false);
		axis.setRange(minimum, maximum);
		return axis;
	}

	protected NumberAxis createAxis(double minimum, double maximum) {
		NumberAxis axis = new NumberAxis();
		axis.setAutoRange(false);
		axis.setRange(minimum, maximum);
		return axis;
	}

	public void setData(double[] xValues, double[] yValues) {
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("data", new double[][]{xValues, yValues});
		getPlot().setDataset(dataset);
	}

	public void setMaximumDomainAxisValue(double maximum) {
		NumberAxis domainAxis = (NumberAxis) getPlot().getDomainAxis();
		//domainRange.setRange(0, maximum);

		double unit = maximum / 16;

		if (unit > 0.65) //for max graph frequency > 12 Hz
		{
			unit = Math.round(unit);
		} else if (unit > 0.25) //for max graph frequency > 4.0 Hz
		{
			unit = 0.5;
		} else if (unit > 0.1) //for max graph frequency > 1.6 Hz
		{
			unit = 0.25;
		} else {
			unit = 0.1;
		}

		domainAxis.setRange(0, maximum);
		domainAxis.setTickUnit(new NumberTickUnit(unit));
	}

	public double getMaximumDomainAxisValue() {
		NumberAxis domainAxis = (NumberAxis) getPlot().getDomainAxis();
		return domainAxis.getRange().getUpperBound();
	}

	public abstract String getDomainAxisName();
}
