/* RocDialog.java created 2007-12-18
 *
 */

package org.signalml.app.view.roc;

import static org.signalml.app.SvarogApplication._;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RectangleInsets;
import org.signalml.app.action.ExportChartToClipboardAction;
import org.signalml.app.action.ExportChartToFileAction;
import org.signalml.app.model.PropertySheetModel;
import org.signalml.app.model.TableToTextExporter;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.ViewerPropertySheet;
import org.signalml.domain.roc.RocData;
import org.signalml.plugin.export.SignalMLException;

/** RocDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RocDialog extends org.signalml.app.view.dialog.AbstractSvarogDialog  {

	private static final int AXIS_SPACE_SIZE = 40;

	public static final int ROC_PLOT_SIZE = 400;

	private static final long serialVersionUID = 1L;

	private TableToTextExporter tableToTextExporter;
	private ViewerFileChooser fileChooser;

	private XYShapeHighlightingRenderer plotRenderer;
	private XYPlot rocPlot;
	private JFreeChart rocChart;
	private ChartPanel chartPanel;

	private JPopupMenu chartPopupMenu;

	private RocTableModel rocTableModel;
	private RocTable rocTable;

	private PropertySheetModel rocDataPropertySheetModel;
	private PropertySheetModel rocDataPointPropertySheetModel;

	private ViewerPropertySheet rocDataPropertySheet;
	private ViewerPropertySheet rocDataPointPropertySheet;

	private RocData rocData;

	public  RocDialog() {
		super();
	}

	public  RocDialog( Window w, boolean isModal) {
		super( w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("Roc result"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/roc.png"));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		JPanel chartContainPanel = new JPanel(new BorderLayout());
		chartContainPanel.setBorder(new CompoundBorder(
		                                    new TitledBorder(_("Roc curve chart")),
		                                    new EmptyBorder(3,3,3,3)
		                            ));

		chartContainPanel.add(getChartPanel(), BorderLayout.CENTER);

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBorder(new CompoundBorder(
		                             new TitledBorder(_("Roc parameters")),
		                             new EmptyBorder(3,3,3,3)
		                     ));

		JScrollPane scrollPane = new JScrollPane(getRocTable());
		tablePanel.add(scrollPane, BorderLayout.CENTER);

		scrollPane.setPreferredSize(new Dimension(400,300));

		JPanel topPanel = new JPanel(new BorderLayout());

		topPanel.add(chartContainPanel, BorderLayout.WEST);
		topPanel.add(tablePanel, BorderLayout.CENTER);

		JPanel propertySheetPanel = new JPanel(new GridLayout(1,2,0,0));

		JPanel rocDataPropertyPanel = new JPanel(new BorderLayout());
		rocDataPropertyPanel.setBorder(new CompoundBorder(
		                                       new TitledBorder(_("Roc properties")),
		                                       new EmptyBorder(3,3,3,3)
		                               ));
		JScrollPane rocDataPropertySheetScrollPane = new JScrollPane(getRocDataPropertySheet());
		rocDataPropertySheetScrollPane.setPreferredSize(new Dimension(400,220));
		rocDataPropertyPanel.add(rocDataPropertySheetScrollPane, BorderLayout.CENTER);

		JPanel rocDataPointPropertyPanel = new JPanel(new BorderLayout());
		rocDataPointPropertyPanel.setBorder(new CompoundBorder(
		                new TitledBorder(_("Roc point properties")),
		                new EmptyBorder(3,3,3,3)
		                                    ));
		JScrollPane rocDataPointPropertySheetScrollPane = new JScrollPane(getRocDataPointPropertySheet());
		rocDataPointPropertySheetScrollPane.setPreferredSize(new Dimension(400,220));
		rocDataPointPropertyPanel.add(rocDataPointPropertySheetScrollPane, BorderLayout.CENTER);

		propertySheetPanel.add(rocDataPropertyPanel);
		propertySheetPanel.add(rocDataPointPropertyPanel);

		interfacePanel.add(topPanel, BorderLayout.NORTH);
		interfacePanel.add(propertySheetPanel, BorderLayout.CENTER);

		return interfacePanel;

	}

	public XYShapeHighlightingRenderer getPlotRenderer() {
		if (plotRenderer == null) {
			plotRenderer = new XYShapeHighlightingRenderer(true, true, 1);

			plotRenderer.setSeriesPaint(0, Color.LIGHT_GRAY);
			plotRenderer.setSeriesStroke(0, new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10F, new float[] { 10, 10 }, 0F));

			plotRenderer.setSeriesPaint(1, Color.RED);

			plotRenderer.setSeriesPaint(2, Color.RED);
			plotRenderer.setSeriesStroke(2, new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10F, new float[] { 1, 3 }, 0F));

			plotRenderer.setSeriesPaint(3, Color.RED);
			plotRenderer.setSeriesStroke(3, new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10F, new float[] { 1, 3 }, 0F));

		}
		return plotRenderer;
	}

	public XYPlot getRocPlot() {
		if (rocPlot == null) {

			NumberAxis xAxis = new NumberAxis();
			xAxis.setAutoRange(false);
			xAxis.setRange(0,1);
			xAxis.setTickUnit(new NumberTickUnit(0.2));
			xAxis.setLabel(_("False positive rate"));

			NumberAxis yAxis = new NumberAxis();
			yAxis.setAutoRange(false);
			yAxis.setRange(0,1);
			yAxis.setTickUnit(new NumberTickUnit(0.2));
			yAxis.setLabel(_("True positive rate"));

			rocPlot = new XYPlot(null, xAxis, yAxis, getPlotRenderer());
			AxisSpace axisSpace = new AxisSpace();
			axisSpace.setBottom(AXIS_SPACE_SIZE);
			axisSpace.setLeft(AXIS_SPACE_SIZE);
			rocPlot.setFixedDomainAxisSpace(axisSpace);
			rocPlot.setFixedRangeAxisSpace(axisSpace);


		}
		return rocPlot;
	}

	public JFreeChart getRocChart() {
		if (rocChart == null) {
			rocChart = new JFreeChart(null, null, getRocPlot(), false);
			rocChart.setBorderVisible(true);
			rocChart.setBackgroundPaint(Color.WHITE);
			rocChart.setPadding(new RectangleInsets(5,3,3,5));
		}
		return rocChart;
	}

	public ChartPanel getChartPanel() {
		if (chartPanel == null) {

			chartPanel = new ChartPanel(getRocChart());
			chartPanel.setDomainZoomable(false);
			chartPanel.setRangeZoomable(false);
			chartPanel.setMouseZoomable(false);
			chartPanel.setPopupMenu(getChartPopupMenu());
			chartPanel.setBackground(Color.WHITE);
			chartPanel.setPreferredSize(new Dimension(ROC_PLOT_SIZE, ROC_PLOT_SIZE));
			chartPanel.setMinimumSize(new Dimension(ROC_PLOT_SIZE, ROC_PLOT_SIZE));
			chartPanel.setMaximumSize(new Dimension(ROC_PLOT_SIZE, ROC_PLOT_SIZE));

		}
		return chartPanel;
	}

	public RocTableModel getRocTableModel() {
		if (rocTableModel == null) {
			rocTableModel = new RocTableModel();
		}
		return rocTableModel;
	}

	public RocTable getRocTable() {
		if (rocTable == null) {
			rocTable = new RocTable(getRocTableModel());
			rocTable.setTableToTextExporter(tableToTextExporter);
			rocTable.setFileChooser(fileChooser);

			rocTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					int index = rocTable.getSelectedRow();
					if (index < 0) {
						getPlotRenderer().clearHighlight();
						getRocDataPointPropertySheetModel().setSubject(null);
					} else {
						getPlotRenderer().setHighlight(1, index);
						getRocDataPointPropertySheetModel().setSubject(rocData.getRocDataPointAt(index));
					}

				}

			});
		}
		return rocTable;
	}

	public JPopupMenu getChartPopupMenu() {
		if (chartPopupMenu == null) {
			chartPopupMenu = new JPopupMenu();
			chartPopupMenu.add(new JMenuItem(new ExportRocChartToClipboardAction()));
			chartPopupMenu.add(new JMenuItem(new ExportRocChartToFileAction()));
		}
		return chartPopupMenu;
	}

	public PropertySheetModel getRocDataPropertySheetModel() {
		if (rocDataPropertySheetModel == null) {
			rocDataPropertySheetModel = new PropertySheetModel();
			rocDataPropertySheetModel.setNumberFormat(new DecimalFormat("0.###"));
		}
		return rocDataPropertySheetModel;
	}

	public PropertySheetModel getRocDataPointPropertySheetModel() {
		if (rocDataPointPropertySheetModel == null) {
			rocDataPointPropertySheetModel = new PropertySheetModel();
			rocDataPointPropertySheetModel.setNumberFormat(new DecimalFormat("0.###"));
		}
		return rocDataPointPropertySheetModel;
	}

	public ViewerPropertySheet getRocDataPropertySheet() {
		if (rocDataPropertySheet == null) {
			rocDataPropertySheet = new ViewerPropertySheet(getRocDataPropertySheetModel());
			TableColumnModel columnModel = rocDataPropertySheet.getColumnModel();
			columnModel.getColumn(0).setPreferredWidth(200);
			columnModel.getColumn(1).setPreferredWidth(50);
		}
		return rocDataPropertySheet;
	}

	public ViewerPropertySheet getRocDataPointPropertySheet() {
		if (rocDataPointPropertySheet == null) {
			rocDataPointPropertySheet = new ViewerPropertySheet(getRocDataPointPropertySheetModel());
			TableColumnModel columnModel = rocDataPointPropertySheet.getColumnModel();
			columnModel.getColumn(0).setPreferredWidth(200);
			columnModel.getColumn(1).setPreferredWidth(50);
		}
		return rocDataPointPropertySheet;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		rocData = (RocData) model;
		DefaultXYDataset dataset = new DefaultXYDataset();

		double[] falseRates = rocData.getFalseRates();
		double[] trueRates = rocData.getTrueRates();
		double[][] reference = new double[][] { {0,1}, {0,1} };
		double[][] data = new double[][] { falseRates, trueRates };

		dataset.addSeries("reference", reference);
		dataset.addSeries("data", data);

		if (falseRates.length > 0) {

			double[][] leadIn = new double[][] { {0, falseRates[0]}, {0, trueRates[0]} };
			double[][] leadOut = new double[][] { { falseRates[falseRates.length-1], 1 }, { trueRates[trueRates.length-1], 1 } };

			dataset.addSeries("leadin", leadIn);
			dataset.addSeries("leadout", leadOut);

		}

		getRocPlot().setDataset(dataset);
		getRocTableModel().setRocData(rocData);
		getRocDataPropertySheetModel().setSubject(rocData);
		getRocDataPointPropertySheetModel().setSubject(null);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// nothing to do
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return RocData.class.isAssignableFrom(clazz);
	}

	public TableToTextExporter getTableToTextExporter() {
		return tableToTextExporter;
	}

	public void setTableToTextExporter(TableToTextExporter tableToTextExporter) {
		this.tableToTextExporter = tableToTextExporter;
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser viewerFileChooser) {
		this.fileChooser = viewerFileChooser;
	}

	protected class ExportRocChartToFileAction extends ExportChartToFileAction {

		private static final long serialVersionUID = 1L;

		private ExportRocChartToFileAction() {
			super();
			setFileChooser(fileChooser);
			setOptionPaneParent(RocDialog.this);
		}

		@Override
		protected JFreeChart getChart() {
			return getRocChart();
		}

		@Override
		protected Dimension getImageSize() {
			return new Dimension(RocDialog.ROC_PLOT_SIZE, RocDialog.ROC_PLOT_SIZE);
		}

	}

	protected class ExportRocChartToClipboardAction extends ExportChartToClipboardAction {

		private static final long serialVersionUID = 1L;

		private ExportRocChartToClipboardAction() {
			super();
		}

		@Override
		protected JFreeChart getChart() {
			return getRocChart();
		}

		@Override
		protected Dimension getImageSize() {
			return new Dimension(RocDialog.ROC_PLOT_SIZE, RocDialog.ROC_PLOT_SIZE);
		}

	}

}
