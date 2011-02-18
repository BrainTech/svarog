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
 * An abstract class for easier creating and usage of a chart panel containing
 * a filter response.
 *
 * @author Piotr Szachewicz
 */
public abstract class ResponseChartPanel extends ChartPanel {

	/**
	 * A message source accessor capable of resolving localized message codes.
	 */
	protected final MessageSourceAccessor messageSource;

	/**
	 * Creates a new chart panel.
	 * @param messageSource message source capable of resolving
	 * localized message codes
	 */
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

	/**
	 * Sets the title of the plot.
	 * @param titleText new title
	 */
	public void setTitle(String titleText) {
		TextTitle title = new TextTitle(titleText, createDefaultFont());
		getChart().setTitle(title);
	}

	/**
	 * Creates a chart for this chart panel.
	 * @return the created chart
	 */
	protected static JFreeChart createChart() {
		JFreeChart newChart = new JFreeChart(createPlot());

		newChart.setBorderVisible(true);
		newChart.setBackgroundPaint(Color.WHITE);
		newChart.setPadding(new RectangleInsets(5, 5, 5, 5));
		newChart.removeLegend();

		return newChart;
	}

	/**
	 * Creates a plot for this chart panel.
	 * @return the created plot
	 */
	protected static XYPlot createPlot() {
		XYPlot newPlot = new XYPlot();

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		newPlot.setRenderer(renderer);

		return newPlot;
	}

	/**
	 * Creates the domain axis (x-axis) for the chart.
	 * @return the created domain axis
	 */
	protected NumberAxis createDomainAxis() {
		NumberAxis domainAxis = new NumberAxis();
		domainAxis.setAutoRange(false);
		return domainAxis;
	}

	/**
	 * Returns whether the range axis is logarithmic.
	 * @return true if the range axis should be logarithmic, false otherwise
	 */
	protected boolean isRangeAxisLogarithmic() {
		return false;
	}

	/**
	 * Creates the default font which can be used for this chart panel.
	 * @return the default font
	 */
	protected Font createDefaultFont() {
		return new Font(Font.DIALOG, Font.PLAIN, 12);
	}

	/**
	 * Returns the plot contained in this chart panel.
	 * @return the plot contained in this chart panel
	 */
	protected XYPlot getPlot() {
		return getChart().getXYPlot();
	}

	/**
	 * Creates the range axis (y-axis) for the chart contained in this
	 * chart panel.
	 * @return the range axis for this chart
	 */
	protected abstract NumberAxis createRangeAxis();

	/**
	 * Creates a logarithmic axis.
	 * @param minimum minimum value to be shown on the axis
	 * @param maximum maximum value to be shown on the axis
	 * @return the created axis
	 */
	protected NumberAxis createLogarithmicAxis(double minimum, double maximum) {
		NumberAxis axis = new LogarithmicAxis("");
		axis.setAutoRange(false);
		((LogarithmicAxis) axis).setStrictValuesFlag(false);
		axis.setRange(minimum, maximum);
		return axis;
	}

	/**
	 * Creates a non-logarithmic axis.
	 * @param minimum minimum value to be shown on the axis
	 * @param maximum maximum value to be shown on the axis
	 * @return the created axis
	 */
	protected NumberAxis createNonLogarithmicAxis(double minimum, double maximum) {
		NumberAxis axis = new NumberAxis();
		axis.setAutoRange(false);
		axis.setRange(minimum, maximum);
		return axis;
	}

	/**
	 * Sets the data for the plot contained in this chart panel.
	 * @param xValues an array containing x-values
	 * @param yValues an array containing y-values
	 */
	public void setData(double[] xValues, double[] yValues) {
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("data", new double[][]{xValues, yValues});
		getPlot().setDataset(dataset);
	}

	/**
	 * Sets the maximum value to be shown on the domain axis (x-axis).
	 * @param maximum maximum value to be shown
	 */
	public void setMaximumDomainAxisValue(double maximum) {
		NumberAxis domainAxis = (NumberAxis) getPlot().getDomainAxis();

		double unit = maximum / 16;

		if (unit > 0.65) {
			unit = Math.round(unit);
		} else if (unit > 0.25) {
			unit = 0.5;
		} else if (unit > 0.1) {
			unit = 0.25;
		} else {
			unit = 0.1;
		}

		domainAxis.setRange(0, maximum);
		domainAxis.setTickUnit(new NumberTickUnit(unit));
	}

	/**
	 * Returns the maximum value shown on the domain axis (x-axis).
	 * @return maximum value shown
	 */
	public double getMaximumDomainAxisValue() {
		NumberAxis domainAxis = (NumberAxis) getPlot().getDomainAxis();
		return domainAxis.getRange().getUpperBound();
	}

	/**
	 * Returns the name of the domain axis (it will be shown on the chart).
	 * This method should be overloaded to change the name.
	 * @return the name of the the domain axis
	 */
	public abstract String getDomainAxisName();

}
