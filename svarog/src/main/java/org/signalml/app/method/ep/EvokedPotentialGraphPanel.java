package org.signalml.app.method.ep;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import org.signalml.app.action.components.ExportAllChartsToFileAction;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.app.view.common.dialogs.OptionPane;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorWriter;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;
import org.signalml.method.ep.EvokedPotentialResult;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.util.Util;

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

	private ExportAllEPChartsToFileAction exportAllEPChartsToFileAction;
	private ExportAllEPSamplesToFloatFileAction exportAllEPSamplesToFloatFileAction;

	public EvokedPotentialGraphPanel(ViewerFileChooser fileChooser) {
		super();
		this.fileChooser = fileChooser;

		titleFont = new Font(Font.DIALOG, Font.PLAIN, 10);
		normalRenderer = new XYLineAndShapeRenderer(true, false);
		normalRenderer.setSeriesPaint(0, Color.BLUE);
		normalRenderer.setSeriesPaint(1, Color.RED);

		exportAllEPChartsToFileAction = new ExportAllEPChartsToFileAction();
		exportAllEPSamplesToFloatFileAction = new ExportAllEPSamplesToFloatFileAction();

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

			exportAllEPChartsToFileAction.setEnabledAsNeeded();
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

	public ExportAllEPChartsToFileAction getExportAllEPChartsToFileAction() {
		return exportAllEPChartsToFileAction;
	}

	public ExportAllEPSamplesToFloatFileAction getExportAllEPSamplesToFloatFileAction() {
		return exportAllEPSamplesToFloatFileAction;
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

		int channel, e;

		int channelCount = result.getChannelCount();
		int sampleCount = result.getSampleCount();

		charts = new JFreeChart[channelCount];

		globalMin = Double.MAX_VALUE;
		globalMax = Double.MIN_VALUE;

		for (double[][] samples: result.getAverageSamples()) {

			for (channel=0; channel<channelCount; channel++) {
				for (e=0; e<sampleCount; e++) {

					if (samples[channel][e] < globalMin) {
						globalMin = samples[channel][e];
					}
					if (samples[channel][e] > globalMax) {
						globalMax = samples[channel][e];
					}

				}

			}
		}

		timeValues = new double[sampleCount];
		float samplingFrequency = result.getSamplingFrequency();

		for (e=0; e<sampleCount; e++) {
			timeValues[e] = (((double) e) / samplingFrequency) - result.getSecondsBefore();
		}

		for (channel=0; channel<channelCount; channel++) {
			List<double[]> channelSamples = new ArrayList<double[]>();

			for (int i = 0; i < result.getAverageSamples().size(); i++) {
				double[][] data = result.getAverageSamples().get(i);
				channelSamples.add(data[channel]);
			}

			if (channel == channelCount-1) {
				charts[channel] = createChart(timeValues, channelSamples, globalMin, globalMax, result.getLabels()[channel], ChartType.BOTTOM);
			}
			else if (channel == 0 && channelCount != 1) {
				charts[channel] = createChart(timeValues, channelSamples, globalMin, globalMax, result.getLabels()[channel], ChartType.TOP);
			} else {
				charts[channel] = createChart(timeValues, channelSamples, globalMin, globalMax, result.getLabels()[channel], ChartType.STRIPPED);
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

	JFreeChart createChart(double[] timeValues, List<double[]> data, double minY, double maxY, String label, ChartType type) {

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
			//double[][] data = dataList.get(i);
			dataset.addSeries("data" + i, new double[][] { timeValues, data.get(i) } );
		}
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

	protected class ExportAllEPSamplesToFloatFileAction extends AbstractSignalMLAction {

		public ExportAllEPSamplesToFloatFileAction() {
			super();
			setText(_("Save all samples as txt file"));
			setToolTip(_("Save all samples as one TXT file"));
			setIconPath("org/signalml/app/icon/script_save.png");
		}

		@Override
		public void actionPerformed(ActionEvent event) {

			List<TagStyleGroup> averagedTagStyles = result.getData().getParameters().getAveragedTagStyles();
			TagStyleGroup selectedGroup = averagedTagStyles.get(0);
			if (averagedTagStyles.size() > 1)
				selectedGroup = showTagStyleGroupSelection();

			//selected index
			int i;
			for (i = 0; i < averagedTagStyles.size(); i++) {
				if (averagedTagStyles.get(i).equals(selectedGroup))
					break;
			}

			// file selection
			File file = showFileChooserDialog();
			if (file == null)
				return;

			try {
				writeData(file, i);
			} catch (IOException e) {
				Dialogs.showExceptionDialog(e);
				e.printStackTrace();
				return;
			}

		}

		protected TagStyleGroup showTagStyleGroupSelection() {
			SelectTagGroupDialog dialog = new SelectTagGroupDialog();
			List<TagStyleGroup> averagedTagStyles = result.getData().getParameters().getAveragedTagStyles();
			List<TagStyleGroup> selectedGroups = new ArrayList<TagStyleGroup>();
			selectedGroups.addAll(averagedTagStyles);

			boolean okPressed = dialog.showDialog(selectedGroups);

			if (!okPressed)
				return null;

			return selectedGroups.get(0);
		}

		protected File showFileChooserDialog() {
			File file;
			boolean hasFile = false;
			do {
				file = fileChooser.chooseSamplesSaveAsTextFile(null);
				if (file == null) {
					return null;
				}
				String ext = Util.getFileExtension(file, false);
				if (ext == null) {
					file = new File(file.getAbsolutePath() + ".bin");
				}

				hasFile = true;

				if (file.exists()) {
					int res = OptionPane.showFileAlreadyExists(null);
					if (res != OptionPane.OK_OPTION) {
						hasFile = false;
					}
				}

			} while (!hasFile);

			return file;
		}

		protected void writeData(File file, int groupIndex) throws IOException {
			double[][] samples = result.getAverageSamples().get(groupIndex);
			int channelCount = samples.length;
			int sampleCount = samples[0].length;
			DoubleArraySampleSource sampleSource = new DoubleArraySampleSource(samples, channelCount, sampleCount);

			RawSignalWriter rawSignalWriter = new RawSignalWriter();
			SignalExportDescriptor signalExportDescriptor = new SignalExportDescriptor();
			rawSignalWriter.writeSignal(file, sampleSource, signalExportDescriptor, null);

			RawSignalDescriptorWriter descriptorWriter = new RawSignalDescriptorWriter();
			RawSignalDescriptor descriptor = new RawSignalDescriptor();
			descriptor.setSampleCount(sampleCount);
			descriptor.setChannelCount(channelCount);
			descriptor.setExportFileName(file.getName());
			descriptor.setChannelLabels(result.getLabels());

			File xmlFile = Util.changeOrAddFileExtension(file, "xml");
			descriptorWriter.writeDocument(descriptor, xmlFile);
		}

		@Override
		public void setEnabledAsNeeded() {
			setEnabled(result != null);
		}

	}

}

class SelectTagGroupDialog extends AbstractDialog {

	private JComboBox selectTagGroupComboBox;

	public SelectTagGroupDialog() {
		super();
		setModal(true);
		setLocationRelativeTo(null);
	}

	@Override
	protected JComponent createInterface() {
		AbstractPanel panel = new AbstractPanel(_("Select tag group"));
		panel.setLayout(new BorderLayout());
		panel.add(getSelectTagGroupComboBox(), BorderLayout.CENTER);
		return panel;
	}

	public JComboBox getSelectTagGroupComboBox() {
		if (selectTagGroupComboBox == null) {
			selectTagGroupComboBox = new JComboBox();
		}
		return selectTagGroupComboBox;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return true;
	}

	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {
		List<TagStyleGroup> groups = (List<TagStyleGroup>) model;
		getSelectTagGroupComboBox().setModel(new DefaultComboBoxModel(groups.toArray(new TagStyleGroup[0])));
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		List<TagStyleGroup> groups = (List<TagStyleGroup>) model;
		groups.clear();
		groups.add((TagStyleGroup) selectTagGroupComboBox.getSelectedItem());
	}

}