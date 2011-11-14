/* EvokedPotentialGraphPanel.java created 2008-01-14
 *
 */

package org.signalml.app.method.ep;

import static org.signalml.app.SvarogApplication._;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.Scrollable;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.signalml.app.action.ExportAllChartsToClipboardAction;
import org.signalml.app.action.ExportAllChartsToFileAction;
import org.signalml.app.action.ExportChartToClipboardAction;
import org.signalml.app.action.ExportChartToFileAction;
import org.signalml.app.action.ExportSamplesToClipboardAction;
import org.signalml.app.action.ExportSamplesToFileAction;
import org.signalml.app.action.ExportSamplesToMultiplexedFloatFileAction;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.method.ep.EvokedPotentialResult;

/** EvokedPotentialGraphPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialGraphPanel extends JComponent implements Scrollable {

	private static final long serialVersionUID = 1L;

	public static final int CHART_HEIGHT = 100;
	public static final int PREFERRED_CHART_WIDTH = 600;
	public static final int AXIS_SPACE = 35;

	private static enum ChartType {
		NORMAL,
		STRIPPED,
		TOP,
		BOTTOM
	}
	ViewerFileChooser fileChooser;

	EvokedPotentialResult result;

	JFreeChart[] charts;
	Rectangle[] bounds;
	int focusedChartIndex = -1;

	private Font titleFont;
	private XYLineAndShapeRenderer normalRenderer;

	double[] timeValues;

	double globalMin;
	double globalMax;

	private JPopupMenu popupMenu;

	private ExportEPChartToClipboardAction exportChartToClipboardAction;
	private ExportEPChartToFileAction exportChartToFileAction;

	private ExportAllEPChartsToClipboardAction exportAllEPChartsToClipboardAction;
	private ExportAllEPChartsToFileAction exportAllEPChartsToFileAction;

	private ExportAllEPSamplesToClipboardAction exportAllEPSamplesToClipboardAction;
	private ExportAllEPSamplesToFileAction exportAllEPSamplesToFileAction;

	private ExportEPSamplesToClipboardAction exportEPSamplesToClipboardAction;
	private ExportEPSamplesToFileAction exportEPSamplesToFileAction;

	private ExportAllEPSamplesToFloatFileAction exportAllEPSamplesToFloatFileAction;

	public EvokedPotentialGraphPanel( ViewerFileChooser fileChooser) {
		super();
		this.fileChooser = fileChooser;

		titleFont = new Font(Font.DIALOG, Font.PLAIN, 10);
		normalRenderer = new XYLineAndShapeRenderer(true, false);
		normalRenderer.setSeriesPaint(0, Color.BLUE);
		normalRenderer.setSeriesPaint(1, Color.RED);

		exportChartToClipboardAction = new ExportEPChartToClipboardAction();
		exportChartToFileAction = new ExportEPChartToFileAction();
		exportEPSamplesToClipboardAction = new ExportEPSamplesToClipboardAction();
		exportEPSamplesToFileAction = new ExportEPSamplesToFileAction();
		exportAllEPChartsToClipboardAction = new ExportAllEPChartsToClipboardAction();
		exportAllEPChartsToFileAction = new ExportAllEPChartsToFileAction();
		exportAllEPSamplesToClipboardAction = new ExportAllEPSamplesToClipboardAction();
		exportAllEPSamplesToFileAction = new ExportAllEPSamplesToFileAction();
		exportAllEPSamplesToFloatFileAction = new ExportAllEPSamplesToFloatFileAction();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				focusedChartIndex = getChartIndexAtPoint(e.getPoint());
				maybeShowPopupMenu(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopupMenu(e);
			}

			private void maybeShowPopupMenu(MouseEvent e) {
				if (e.isPopupTrigger()) {
					JPopupMenu popupMenu = getPanelPopupMenu();
					if (popupMenu != null) {
						popupMenu.show(e.getComponent(),e.getX(),e.getY());
					}
				}
			}

		});

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				assignBounds();
			}

		});

	}

	public EvokedPotentialResult getResult() {
		return result;
	}

	public void setResult(EvokedPotentialResult result) {
		if (this.result != result) {
			this.result = result;
			charts = null;

			exportAllEPChartsToClipboardAction.setEnabledAsNeeded();
			exportAllEPChartsToFileAction.setEnabledAsNeeded();
			exportAllEPSamplesToClipboardAction.setEnabledAsNeeded();
			exportAllEPSamplesToFileAction.setEnabledAsNeeded();
			exportAllEPSamplesToFloatFileAction.setEnabledAsNeeded();

			revalidate();
			repaint();
		}
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public int getFocusedChartIndex() {
		return focusedChartIndex;
	}

	public ExportAllEPChartsToClipboardAction getExportAllEPChartsToClipboardAction() {
		return exportAllEPChartsToClipboardAction;
	}

	public ExportAllEPChartsToFileAction getExportAllEPChartsToFileAction() {
		return exportAllEPChartsToFileAction;
	}

	public ExportAllEPSamplesToClipboardAction getExportAllEPSamplesToClipboardAction() {
		return exportAllEPSamplesToClipboardAction;
	}

	public ExportAllEPSamplesToFileAction getExportAllEPSamplesToFileAction() {
		return exportAllEPSamplesToFileAction;
	}

	public ExportAllEPSamplesToFloatFileAction getExportAllEPSamplesToFloatFileAction() {
		return exportAllEPSamplesToFloatFileAction;
	}

	public JPopupMenu getPanelPopupMenu() {

		if (popupMenu == null) {

			popupMenu = new JPopupMenu();
			popupMenu.add(exportChartToClipboardAction);
			popupMenu.add(exportChartToFileAction);
			popupMenu.add(exportEPSamplesToClipboardAction);
			popupMenu.add(exportEPSamplesToFileAction);
			popupMenu.addSeparator();

			JMenu allMenu = new JMenu(_("All channels"));

			allMenu.add(exportAllEPChartsToClipboardAction);
			allMenu.add(exportAllEPChartsToFileAction);
			allMenu.add(exportAllEPSamplesToClipboardAction);
			allMenu.add(exportAllEPSamplesToFileAction);
			allMenu.addSeparator();
			allMenu.add(exportAllEPSamplesToFloatFileAction);

			popupMenu.add(allMenu);

		}

		exportChartToClipboardAction.setEnabledAsNeeded();
		exportChartToFileAction.setEnabledAsNeeded();
		exportEPSamplesToClipboardAction.setEnabledAsNeeded();
		exportEPSamplesToFileAction.setEnabledAsNeeded();

		return popupMenu;

	}

	int getChartIndexAtPoint(Point point) {

		if (result == null) {
			return -1;
		}

		int channelCount = result.getChannelCount();
		for (int i=0; i<channelCount; i++) {
			if (bounds[i].contains(point)) {
				return i;
			}
		}

		return -1;

	}

	private void createCharts() {

		int i, e;

		int channelCount = result.getChannelCount();
		int sampleCount = result.getSampleCount();

		charts = new JFreeChart[channelCount];

		globalMin = Double.MAX_VALUE;
		globalMax = Double.MIN_VALUE;

		double[][] samples = result.getAverageSamples();

		for (i=0; i<channelCount; i++) {

			for (e=0; e<sampleCount; e++) {

				if (samples[i][e] < globalMin) {
					globalMin = samples[i][e];
				}
				if (samples[i][e] > globalMax) {
					globalMax = samples[i][e];
				}

			}

		}

		timeValues = new double[sampleCount];
		float samplingFrequency = result.getSamplingFrequency();

		for (e=0; e<sampleCount; e++) {
			timeValues[e] = (((double) e) / samplingFrequency) - result.getSecondsBefore();
		}

		for (i=0; i<channelCount; i++) {

			if (i == channelCount-1) {
				charts[i] = createChart(new double[][] { timeValues, samples[i] }, globalMin, globalMax, result.getLabels()[i], ChartType.BOTTOM);
			}
			else if (i == 0 && channelCount != 1) {
				charts[i] = createChart(new double[][] { timeValues, samples[i] }, globalMin, globalMax, result.getLabels()[i], ChartType.TOP);
			} else {
				charts[i] = createChart(new double[][] { timeValues, samples[i] }, globalMin, globalMax, result.getLabels()[i], ChartType.STRIPPED);
			}

		}

		assignBounds();

	}

	void assignBounds() {

		if (result == null) {
			return;
		}

		int channelCount = result.getChannelCount();

		bounds = new Rectangle[channelCount];

		int y = 0;
		Dimension size = getSize();
		int height;

		for (int i=0; i<channelCount; i++) {

			height = CHART_HEIGHT;

			if ((i == channelCount-1) || (i == 0 && channelCount != 1)) {
				height += AXIS_SPACE;
			}

			bounds[i] = new Rectangle(0, y, size.width, height);

			y += height;

		}

	}

	JFreeChart createChart(double[][] data, double minY, double maxY, String label, ChartType type) {

		double minTime = data[0][0];
		double maxTime = data[0][data[0].length-1];

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
		dataset.addSeries("data", data);
		dataset.addSeries("redline", new double[][] { {0,0}, {minY, maxY} });

		plot = new XYPlot(dataset, xAxis, yAxis, normalRenderer);
		title = new TextTitle(label, titleFont, Color.BLACK, RectangleEdge.LEFT, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, new RectangleInsets(0,0,0,0));

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


		chart = new JFreeChart(null, null, plot, false);
		chart.setTitle(title);
		chart.setBorderVisible(false);
		chart.setBackgroundPaint(Color.WHITE);
		chart.setPadding(new RectangleInsets(0,0,0,6));

		return chart;

	}

	@Override
	public boolean isOpaque() {
		return true;
	}

	@Override
	public boolean isDoubleBuffered() {
		return true;
	}

	@Override
	protected void paintComponent(Graphics gOrig) {

		Graphics2D g = (Graphics2D) gOrig;

		Rectangle clip = g.getClipBounds();

		g.setColor(getBackground());
		g.fill(clip);

		if (result == null) {
			return;
		}
		if (charts == null) {
			createCharts();
		}

		for (int i=0; i<charts.length; i++) {

			if (bounds[i].intersects(clip)) {
				charts[i].draw(g, bounds[i]);
			}

		}

	}


	@Override
	public Dimension getPreferredSize() {
		if (result == null) {
			return new Dimension(0,0);
		}

		int channelCount = result.getChannelCount();
		return new Dimension(PREFERRED_CHART_WIDTH, channelCount * CHART_HEIGHT + (channelCount > 1 ? 2 * AXIS_SPACE : AXIS_SPACE));
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		Dimension preferredSize = getPreferredSize();
		return new Dimension(preferredSize.width, Math.min(preferredSize.height, 400));
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return CHART_HEIGHT;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return CHART_HEIGHT / 10;
	}

	protected class ExportEPChartToFileAction extends ExportChartToFileAction {

		private static final long serialVersionUID = 1L;

		ExportEPChartToFileAction() {
			super();
			setFileChooser(fileChooser);
			setOptionPaneParent(EvokedPotentialGraphPanel.this.getTopLevelAncestor());
		}

		@Override
		protected JFreeChart getChart() {
			return createChart(new double[][] { timeValues, result.getAverageSamples()[focusedChartIndex] }, globalMin, globalMax, result.getLabels()[focusedChartIndex], ChartType.NORMAL);
		}

		@Override
		protected Dimension getImageSize() {
			return new Dimension(bounds[focusedChartIndex].width, AXIS_SPACE + CHART_HEIGHT);
		}

		@Override
		public void setEnabledAsNeeded() {
			setEnabled(focusedChartIndex >= 0);
		}

	}

	protected class ExportEPSamplesToFileAction extends ExportSamplesToFileAction {

		private static final long serialVersionUID = 1L;

		ExportEPSamplesToFileAction() {
			super();
			setFileChooser(fileChooser);
			setOptionPaneParent(EvokedPotentialGraphPanel.this.getTopLevelAncestor());
		}

		@Override
		protected int getSampleCount() {
			if (result == null) {
				return 0;
			}
			return result.getSampleCount();
		}

		@Override
		protected double[][] getSamples() {
			if (result == null || focusedChartIndex < 0) {
				return null;
			}
			double[][] samples = new double[2][];
			samples[0] = timeValues;
			samples[1] = result.getAverageSamples()[focusedChartIndex];
			return samples;
		}

		@Override
		protected boolean isWithLabels() {
			return true;
		}

		@Override
		protected String getLabel(int index) {
			if (index == 0) {
				return "T";
			}
			return result.getLabels()[focusedChartIndex];
		}

		@Override
		public void setEnabledAsNeeded() {
			setEnabled(focusedChartIndex >= 0);
		}

	}

	protected class ExportEPChartToClipboardAction extends ExportChartToClipboardAction {

		private static final long serialVersionUID = 1L;

		ExportEPChartToClipboardAction() {
			super();
		}

		@Override
		protected JFreeChart getChart() {
			return createChart(new double[][] { timeValues, result.getAverageSamples()[focusedChartIndex] }, globalMin, globalMax, result.getLabels()[focusedChartIndex], ChartType.NORMAL);
		}

		@Override
		protected Dimension getImageSize() {
			return new Dimension(bounds[focusedChartIndex].width, AXIS_SPACE + CHART_HEIGHT);
		}

		@Override
		public void setEnabledAsNeeded() {
			setEnabled(focusedChartIndex >= 0);
		}

	}

	protected class ExportEPSamplesToClipboardAction extends ExportSamplesToClipboardAction {

		private static final long serialVersionUID = 1L;

		ExportEPSamplesToClipboardAction() {
			super();
		}

		@Override
		protected int getSampleCount() {
			if (result == null) {
				return 0;
			}
			return result.getSampleCount();
		}

		@Override
		protected double[][] getSamples() {
			if (result == null || focusedChartIndex < 0) {
				return null;
			}
			double[][] samples = new double[2][];
			samples[0] = timeValues;
			samples[1] = result.getAverageSamples()[focusedChartIndex];
			return samples;
		}

		@Override
		protected boolean isWithLabels() {
			return true;
		}

		@Override
		protected String getLabel(int index) {
			if (index == 0) {
				return "T";
			}
			return result.getLabels()[focusedChartIndex];
		}

		@Override
		public void setEnabledAsNeeded() {
			setEnabled(focusedChartIndex >= 0);
		}

	}

	protected class ExportAllEPChartsToClipboardAction extends ExportAllChartsToClipboardAction {

		private static final long serialVersionUID = 1L;

		ExportAllEPChartsToClipboardAction() {
			super();
		}

		@Override
		protected JFreeChart getChart(int index) {
			return charts[index];
		}

		@Override
		protected Rectangle getChartBounds(int index) {
			return bounds[index];
		}

		@Override
		protected int getChartCount() {
			if (result == null) {
				return 0;
			}
			return result.getChannelCount();
		}

		@Override
		public void setEnabledAsNeeded() {
			setEnabled(result != null);
		}

	}

	protected class ExportAllEPSamplesToClipboardAction extends ExportSamplesToClipboardAction {

		private static final long serialVersionUID = 1L;

		ExportAllEPSamplesToClipboardAction() {
			super();
			setText(_("Copy all samples to clipboard"));
			setToolTip(_("Copy all samples to clipboard"));
		}

		@Override
		protected int getSampleCount() {
			if (result == null) {
				return 0;
			}
			return result.getSampleCount();
		}

		@Override
		protected double[][] getSamples() {
			if (result == null) {
				return null;
			}
			int channelCount = result.getChannelCount();
			double[][] samples = new double[1+channelCount][];
			samples[0] = timeValues;
			double[][] averageSamples = result.getAverageSamples();
			for (int i=0; i<channelCount; i++) {
				samples[i+1] = averageSamples[i];
			}
			return samples;
		}

		@Override
		protected boolean isWithLabels() {
			return true;
		}

		@Override
		protected String getLabel(int index) {
			if (index == 0) {
				return "T";
			}
			return result.getLabels()[index-1];
		}

		@Override
		public void setEnabledAsNeeded() {
			setEnabled(result != null);
		}

	}

	protected class ExportAllEPChartsToFileAction extends ExportAllChartsToFileAction {

		private static final long serialVersionUID = 1L;

		ExportAllEPChartsToFileAction() {
			super();
			setFileChooser(fileChooser);
			setOptionPaneParent(EvokedPotentialGraphPanel.this.getTopLevelAncestor());
		}

		@Override
		protected JFreeChart getChart(int index) {
			return charts[index];
		}

		@Override
		protected Rectangle getChartBounds(int index) {
			return bounds[index];
		}

		@Override
		protected int getChartCount() {
			if (result == null) {
				return 0;
			}
			return result.getChannelCount();
		}

		@Override
		public void setEnabledAsNeeded() {
			setEnabled(result != null);
		}

	}

	protected class ExportAllEPSamplesToFileAction extends ExportSamplesToFileAction {

		private static final long serialVersionUID = 1L;

		ExportAllEPSamplesToFileAction() {
			super();
			setText(_("Save all samples as txt file"));
			setToolTip(_("Save all samples as one TXT file"));
			setFileChooser(fileChooser);
			setOptionPaneParent(EvokedPotentialGraphPanel.this.getTopLevelAncestor());
		}

		@Override
		protected int getSampleCount() {
			if (result == null) {
				return 0;
			}
			return result.getSampleCount();
		}

		@Override
		protected double[][] getSamples() {
			if (result == null) {
				return null;
			}
			int channelCount = result.getChannelCount();
			double[][] samples = new double[1+channelCount][];
			samples[0] = timeValues;
			double[][] averageSamples = result.getAverageSamples();
			for (int i=0; i<channelCount; i++) {
				samples[i+1] = averageSamples[i];
			}
			return samples;
		}

		@Override
		protected boolean isWithLabels() {
			return true;
		}

		@Override
		protected String getLabel(int index) {
			if (index == 0) {
				return "T";
			}
			return result.getLabels()[index-1];
		}

		@Override
		public void setEnabledAsNeeded() {
			setEnabled(result != null);
		}

	}

	protected class ExportAllEPSamplesToFloatFileAction extends ExportSamplesToMultiplexedFloatFileAction {

		private static final long serialVersionUID = 1L;

		ExportAllEPSamplesToFloatFileAction() {
			super();
			setFileChooser(fileChooser);
			setOptionPaneParent(EvokedPotentialGraphPanel.this.getTopLevelAncestor());
		}

		@Override
		protected int getSampleCount() {
			if (result == null) {
				return 0;
			}
			return result.getSampleCount();
		}

		@Override
		protected double[][] getSamples() {
			if (result == null) {
				return null;
			}
			return result.getAverageSamples();
		}

		@Override
		public void setEnabledAsNeeded() {
			setEnabled(result != null);
		}

	}

}
