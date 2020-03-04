package org.signalml.app.method.ep;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.VerticalAlignment;
import org.jfree.data.xy.DefaultXYDataset;
import org.signalml.app.method.ep.EvokedPotentialGraphPanel.ChartType;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;
import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.method.ep.EvokedPotentialResult;
import org.signalml.plugin.export.signal.TagStyle;

public class EvokedPotentialChart extends AbstractPanel {

	public static final int CHART_HEIGHT = 100;
	public static final int PREFERRED_CHART_WIDTH = 570;
	public static final int AXIS_SPACE = 35;
	public static final int LEGEND_SPACE = 20;

	private XYLineAndShapeRenderer normalRenderer;

	private ChartPanel chartPanel;
	private JFreeChart chart;

	private Font titleFont = new Font(Font.DIALOG, Font.PLAIN, 10);

	public EvokedPotentialChart(double[] timeValues, List<double[]> data, double minY, double maxY, String label, ChartType type, EvokedPotentialResult result) {
		super();
		normalRenderer = new XYLineAndShapeRenderer(true, false);
		normalRenderer.setSeriesPaint(0, Color.BLUE);
		normalRenderer.setSeriesPaint(1, Color.RED);

		chart = createChart(timeValues, data, minY, maxY, label, type, result);

		int height = CHART_HEIGHT;
		int width = PREFERRED_CHART_WIDTH;

		if (type != ChartType.STRIPPED)
			height += AXIS_SPACE;
		if (type == ChartType.TOP)
			height += LEGEND_SPACE;

		chartPanel = new ChartPanel(chart, width, height, 0, 0, 1000, 1000, false, false, false, false, false, false);

		chartPanel.setDomainZoomable(false);
		chartPanel.setRangeZoomable(false);
		chartPanel.setMouseZoomable(false);
		chartPanel.setPopupMenu(null);

		add(chartPanel);
	}

	private JFreeChart createChart(double[] timeValues, List<double[]> data, double minY, double maxY, String label, ChartType type, EvokedPotentialResult result) {

		double minTime = timeValues[0];
		double maxTime = timeValues[timeValues.length-1];

		NumberAxis xAxis;
		NumberAxis yAxis;

		DefaultXYDataset dataset;
		XYPlot plot;
		double yTickUnit;
		TextTitle title;
		AxisSpace axisSpace;

		JFreeChart chart;

		xAxis = new NumberAxis();
		xAxis.setAutoRange(false);
		xAxis.setRange(minTime, maxTime);

		yAxis = new NumberAxis();
		yAxis.setAutoRange(false);

		// configure for nine labels, but round to 10
		yTickUnit = Math.round((maxY-minY)/(8*10)) * 10.0;
		yAxis.setTickUnit(new NumberTickUnit(yTickUnit));
		yAxis.setRange(minY, maxY);

		dataset = new DefaultXYDataset();
		for (int i = 0; i < data.size(); i++) {
			TagStyleGroup tagStyleGroup = result.getData().getParameters().getAveragedTagStyles().get(i);
			dataset.addSeries(tagStyleGroup.toString(), new double[][] { timeValues, data.get(i) } );
		}
		dataset.addSeries("", new double[][] { {0,0}, {minY, maxY} });

		plot = new XYPlot(dataset, xAxis, yAxis, normalRenderer);
		title = new TextTitle(label, titleFont, Color.BLACK, RectangleEdge.LEFT, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, new RectangleInsets(0,0,0,0));

		setColorsForSeries(result, plot);

		switch (type) {

		case BOTTOM :
			xAxis.setLabel(_("Time [s]"));
			xAxis.setLabelFont(titleFont.deriveFont(8));
			axisSpace = new AxisSpace();
			axisSpace.setBottom(AXIS_SPACE);
			plot.setFixedDomainAxisSpace(axisSpace);
			title.setPadding(new RectangleInsets(0,0,AXIS_SPACE,0));
			break;

		case TOP :
			xAxis.setLabel(_("Time [s]"));
			xAxis.setLabelFont(titleFont.deriveFont(8));
			plot.setDomainAxisLocation(AxisLocation.TOP_OR_LEFT);
			axisSpace = new AxisSpace();
			axisSpace.setTop(AXIS_SPACE);
			plot.setFixedDomainAxisSpace(axisSpace);
			title.setPadding(new RectangleInsets(AXIS_SPACE,0,0,0));
			break;

		case STRIPPED :
			xAxis.setVisible(false);
			break;

		case NORMAL :
		default :
			xAxis.setLabel(_("Time [s]"));
			title.setPosition(RectangleEdge.TOP);
			title.setHorizontalAlignment(HorizontalAlignment.CENTER);
			title.setVerticalAlignment(VerticalAlignment.TOP);
			break;

		}

		boolean showLegend = type == ChartType.TOP;
		chart = new JFreeChart(null, null, plot, showLegend);
		chart.setTitle(title);
		chart.setBorderVisible(false);
		chart.setBackgroundPaint(Color.WHITE);
		chart.setPadding(new RectangleInsets(0,0,0,6));
		if (chart.getLegend() != null) {
			int itemToRemove = chart.getPlot().getLegendItems().getItemCount() - 1;
			removeItemFromLegend(itemToRemove, chart);

			chart.getLegend().setPosition(RectangleEdge.TOP);
			chart.getLegend().setBorder(1, 1, 1, 1);
		}

		return chart;
	}


	protected void removeItemFromLegend(int itemToRemove, JFreeChart chart) {

		LegendItemCollection legendItems = chart.getPlot().getLegendItems();
		final LegendItemCollection newLegendItems = new LegendItemCollection();

		for (int i = 0; i < legendItems.getItemCount(); i++) {
			if (itemToRemove != i)
				newLegendItems.add(legendItems.get(i));
		}

		LegendItemSource source = new LegendItemSource() {

			@Override
			public LegendItemCollection getLegendItems() {
				return newLegendItems;
			}
		};

		chart.removeLegend();
		chart.addLegend(new LegendTitle(source));
	}


	protected static void setColorsForSeries(EvokedPotentialResult result, XYPlot plot) {
		List<TagStyleGroup> averagedTagStyles = result.getData().getParameters().getAveragedTagStyles();
		StyledTagSet styledTagSet = result.getData().getStyledTagSet();
		XYItemRenderer renderer = plot.getRenderer();

		for (int i = 0; i < averagedTagStyles.size(); i++) {
			TagStyleGroup tagStyleGroup = averagedTagStyles.get(i);

			//mix colors
			int r = 0, g = 0, b = 0;
			for (int j = 0; j < tagStyleGroup.getNumberOfTagStyles(); j++) {
				TagStyle style = styledTagSet.getStyle(null, tagStyleGroup.getTagStyleNames().get(j));

				Color color = style.getFillColor();

				r += color.getRed();
				g += color.getGreen();
				b += color.getBlue();
			}

			r /= tagStyleGroup.getNumberOfTagStyles();
			g /= tagStyleGroup.getNumberOfTagStyles();
			b /= tagStyleGroup.getNumberOfTagStyles();

			Color mixedColor = new Color(r, g, b);

			renderer.setSeriesPaint(i, mixedColor);
		}
	}

}
