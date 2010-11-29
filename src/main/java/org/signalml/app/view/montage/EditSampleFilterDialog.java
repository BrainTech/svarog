/* EditSampleFilterDialog.java created 2010-09-22
 *
 */

package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.Box;
import javax.swing.BoxLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.RectangleInsets;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.dialog.AbstractPresetDialog;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 * A class representing an abstract dialog for {@link TimeDomainSampleFilter}
 * and {@link FFTSampleFilter} editing.
 *
 * @author Piotr Szachewicz
 */
abstract class EditSampleFilterDialog extends AbstractPresetDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * Contains the sampling frequency of the currently edited signal.
	 */
	private float currentSamplingFrequency;

	/**
	 * A {@link JTextField} which can be used to edit the filter's description.
	 */
	private JTextField descriptionTextField;

	/**
	 * The value of the maximum frequency which should be shown on the
	 * {@link EditSampleFilterDialog#frequencyResponsePlot}.
	 */
	protected double graphFrequencyMax;

	/**
	 * A {@link JSpinner} used to set the value of the
	 * {@link EditSampleFilterDialog#graphFrequencyMax} field.
	 */
	protected JSpinner graphScaleSpinner;

	/**
	 * The filter's frequency response plot.
	 */
	protected XYPlot frequencyResponsePlot;

	/**
	 * A {@link JFreeChart} containing the filter's
	 * {@link EditSampleFilterDialog#frequencyResponsePlot},
	 * a plot title etc.
	 */
	protected JFreeChart frequencyResponseChart;

	/**
	 * A {@link FrequencyResponseChartPanel} containing the
	 * {@link EditSampleFilterDialog#frequencyResponseChart} and allowing
	 * to set highlights for selected frequency bands.
	 */
	protected FrequencyResponseChartPanel frequencyResponseChartPanel;

	/**
	 * The frequency axis for the {@link EditSampleFilterDialog#frequencyResponsePlot}.
	 */
	protected NumberAxis frequencyAxis;

	/**
	 * The gain axis for the {@link EditSampleFilterDialog#frequencyResponsePlot}.
	 */
	protected NumberAxis gainAxis;

	/**
	 * Constructor. Sets the message source, parent window, preset manager
	 * for time domain filters and if this dialog blocks top-level windows.
	 * @param messageSource message source to set
	 * @param presetManager a {@link PresetManager} to manage the presets
	 * configured in this window
	 * @param w the parent window or null if there is no parent
	 * @param isModal true if this dialog should block top-level windows,
	 * false otherwise
	 */
	public EditSampleFilterDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w, boolean isModal) {
		super(messageSource, presetManager, w, isModal);
	}

	/**
	 * Constructor. Sets the message source and a preset manager
	 * for this window.
	 * @param messageSource message source to set
	 * @param presetManager a {@link PresetManager} to manage the presets
	 * configured in this window
	 */
	public EditSampleFilterDialog(MessageSourceAccessor messageSource, PresetManager presetManager) {
		super(messageSource, presetManager);
	}

	@Override
	protected void initialize() {

		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/editfilter.png"));
		setResizable(false);

		super.initialize();

	}

	@Override
	public abstract JComponent createInterface();

	/**
	 * Returns the {@link JPanel} containing a {@link JTextField} for setting
	 * the description for the currently edited filter.
	 * @return the {@link JPanel} with controls to edit the filter's
	 * description
	 */
	public JPanel getDescriptionPanel() {

		JPanel descriptionPanel = new JPanel(new BorderLayout());
		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("editSampleFilter.descriptionTitle")),
		        new EmptyBorder(3, 3, 3, 3)
		);
		descriptionPanel.setBorder(border);

		descriptionPanel.add(getDescriptionTextField());

		return descriptionPanel;

	}

	/**
	 * Returns the {@link JPanel} containing the filter's frequency response
	 * plot and maximum graph frequency spinner.
	 * @return the {@link JPanel} containing a fiter's frequency response
	 * graph
	 */
	public JPanel getGraphPanel() {

		JPanel graphSpinnerPanel = new JPanel();
		graphSpinnerPanel.setLayout(new BoxLayout(graphSpinnerPanel, BoxLayout.X_AXIS));

		graphSpinnerPanel.add(new JLabel(messageSource.getMessage("editSampleFilter.graphSpinnerLabel")));
		graphSpinnerPanel.add(Box.createHorizontalStrut(5));
		graphSpinnerPanel.add(Box.createHorizontalGlue());
		graphSpinnerPanel.add(getGraphScaleSpinner());

		JPanel graphPanel = new JPanel(new BorderLayout(6, 6));

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("editSampleFilter.graphPanelTitle")),
		        new EmptyBorder(3, 3, 3, 3)
		);
		graphPanel.setBorder(border);

		graphPanel.add(getFrequencyResponseChartPanel(), BorderLayout.CENTER);
		graphPanel.add(graphSpinnerPanel, BorderLayout.SOUTH);

		return graphPanel;

	}

	/**
	 * Returns the {@link JTextField} which is shown in this dialog and
	 * can be used to edit the filter's description.
	 * @return the {@link JTextField} to edit the filter's description
	 */
	public JTextField getDescriptionTextField() {

		if (descriptionTextField == null) {
			descriptionTextField = new JTextField();
			descriptionTextField.setPreferredSize(new Dimension(200, 25));
		}
		return descriptionTextField;

	}

	/**
	 * Returns the frequency axis of the filter's frequency response plot.
	 * @return the frequency axis of the filter's frequency response plot
	 */
	public NumberAxis getFrequencyAxis() {

		if (frequencyAxis == null) {

			frequencyAxis = new NumberAxis();
			frequencyAxis.setAutoRange(false);
			frequencyAxis.setLabel(messageSource.getMessage("editSampleFilter.graphFrequencyLabel"));

		}
		return frequencyAxis;

	}

	/**
	 * Returns the gain axis of the filter's frequency response plot.
	 * @return the gain axis of the filter's frequency response plot
	 */
	public abstract NumberAxis getGainAxis();

	/**
	 * Returns the {@link XYPlot} showing the filter's frequency response.
	 * @return the {@link XYPlot} showing the filter's frequency response
	 */
	public XYPlot getFrequencyResponsePlot() {

		if (frequencyResponsePlot == null) {

			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);

			frequencyResponsePlot = new XYPlot(null, getFrequencyAxis(), getGainAxis(), renderer);

		}
		return frequencyResponsePlot;

	}

	/**
	 * Returns the {@link JFreeChart} containing the filter's frequency
	 * response {@link XYPlot}.
	 * @return the {@link JFreeChart} containing the filter's frequency
	 * response {@link XYPlot}
	 */
	public JFreeChart getFrequencyResponseChart() {

		if (frequencyResponseChart == null) {
			frequencyResponseChart = new JFreeChart(messageSource.getMessage("editSampleFilter.graphTitle"), new Font(Font.DIALOG, Font.PLAIN, 12), getFrequencyResponsePlot(), false);
			frequencyResponseChart.setBorderVisible(true);
			frequencyResponseChart.setBackgroundPaint(Color.WHITE);
			frequencyResponseChart.setPadding(new RectangleInsets(5, 5, 5, 5));
		}
		return frequencyResponseChart;

	}

	/**
	 * Returns the {@link FrequencyResponseChartPanel} containing the
	 * {@link EditSampleFilterDialog#frequencyResponseChart}.
	 * @return the {@link FrequencyResponseChartPanel} containing the
	 * {@link EditSampleFilterDialog#frequencyResponseChart}
	 */
	public abstract FrequencyResponseChartPanel getFrequencyResponseChartPanel();

	/**
	 * Returns the {@link JSpinner} which can be used to set the graph
	 * maximum frequency shown.
	 * @return the {@link JSpinner} which can be used to set the graph
	 * maximum frequency shown
	 */
	public JSpinner getGraphScaleSpinner() {

		if (graphScaleSpinner == null) {
			graphScaleSpinner = new JSpinner(new SpinnerNumberModel(0.25, 0.25, 4096.0, 0.25));
			graphScaleSpinner.setPreferredSize(new Dimension(80, 25));

			graphScaleSpinner.setEditor(new JSpinner.NumberEditor(graphScaleSpinner, "0.00"));
			graphScaleSpinner.setFont(graphScaleSpinner.getFont().deriveFont(Font.PLAIN));
		}
		return graphScaleSpinner;

	}

	/**
	 * Returns the sampling frequency for which the filter is being designed.
	 * @return the sampling frequency for the currently edited filter
	 */
	public float getCurrentSamplingFrequency() {
		return currentSamplingFrequency;
	}

	/**
	 * Sets the sampling frequency for which this filter will be designed.
	 * @param currentSamplingFrequency the sampling frequency for the
	 * currently edited filter
	 */
	public void setCurrentSamplingFrequency(float currentSamplingFrequency) {
		this.currentSamplingFrequency = currentSamplingFrequency;
	}

	/**
	 * Returns the maximum frequency shown on the filter's frequency
	 * response plot.
	 * @return the maximum frequency which is shown of the filter's frequency
	 * response plot
	 */
	public double getGraphFrequencyMax() {
		return graphFrequencyMax;
	}

	/**
	 * Sets the maximum frequency shown on the filter's frequency
	 * response plot.
	 * @param graphFrequencyMax maximum frequency to be shown of the filter's
	 * frequency response plot
	 */
	public void setGraphFrequencyMax(double graphFrequencyMax) {

		if (this.graphFrequencyMax != graphFrequencyMax) {

			this.graphFrequencyMax = graphFrequencyMax;

			getGraphScaleSpinner().setValue(graphFrequencyMax);

		}

	}

	/**
	 * Redraws the frequency response plot for the current filter and
	 * sets appropriate values on the frequency axis.
	 */
	protected abstract void updateGraph();

	/**
	 * Updates the rectangle which highlights the selected frequency range.
	 */
	protected abstract void updateHighlights();

	/**
	 * Updates the unit shown for the frequency axis depending on the maximum
	 * graph frequency set.
	 */
	protected void updateFrequencyAxis() {

		double unit = getGraphFrequencyMax() / 16;

		if (unit > 0.65) //for max graph frequency > 12 Hz
			unit = Math.round(unit);
		else if (unit > 0.25) //for max graph frequency > 4.0 Hz
			unit = 0.5;
		else if (unit > 0.1) //for max graph frequency > 1.6 Hz
			unit = 0.25;
		else
			unit = 0.1;

		getFrequencyAxis().setRange(0, getGraphFrequencyMax());
		getFrequencyAxis().setTickUnit(new NumberTickUnit(unit));

	}

	@Override
	public abstract void fillDialogFromModel(Object model) throws SignalMLException;

	@Override
	public abstract void fillModelFromDialog(Object model) throws SignalMLException;

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

		super.validateDialog(model, errors);

		String description = getDescriptionTextField().getText();
		if (description == null || description.isEmpty()) {
			errors.rejectValue("description", "error.editSampleFilter.descriptionEmpty");
		}
		else if (!Util.validateString(description)) {
			errors.rejectValue("description", "error.editSampleFilter.descriptionBadChars");
		}

	}

	@Override
	public abstract boolean supportsModelClass(Class<?> clazz);

	/**
	 * A class representing a {@link ChangeListener} to be used to round
	 * the values set in the {@link JSpinner JSpinners} used in this dialog.
	 */
	protected class SpinnerRoundingChangeListener implements ChangeListener {

		protected boolean lock = false;
		double spinnerStepSize;

		public SpinnerRoundingChangeListener(double spinnerStepSize) {
			this.spinnerStepSize = spinnerStepSize;
		}

		@Override
		public void stateChanged(ChangeEvent e) {

			if (lock) {
				return;
			}

			try {
				lock = true;

				JSpinner spinner = (JSpinner) e.getSource();
				double doubleValue = ((Number) spinner.getValue()).doubleValue();
				double newDoubleValue = ((double) Math.round(doubleValue / spinnerStepSize)) * spinnerStepSize;
				if (newDoubleValue != doubleValue) {
					spinner.setValue(newDoubleValue);
				}

			} finally {
				lock = false;
			}

		}

	}

	/**
	 * A class representing a {@link ChartPanel} which is used to display
	 * the filter's frequency response and highlight currently selected
	 * frequency bands.
	 */
	protected abstract class FrequencyResponseChartPanel extends ChartPanel {

		private static final long serialVersionUID = 1L;

		protected Double startFrequency = null;

		protected int dragHighlightStart = -1;
		protected int dragHighlightEnd = -1;

		protected int selectionHighlightStart = -1;
		protected int selectionHighlightEnd = -1;
		protected boolean hideSelectionHighlight = false;

		public FrequencyResponseChartPanel(JFreeChart chart) {

			super(chart);

			setDomainZoomable(false);
			setRangeZoomable(false);
			setMouseZoomable(false);
			setPopupMenu(null);

		}

		protected double getFrequency(Point p) {

			Rectangle2D area = getScreenDataArea();

			int xMin = (int) Math.floor(area.getX());
			int xMax = (int) Math.ceil(area.getX() + area.getWidth());

			if (p.x < xMin) {
				return 0;
			}
			if (p.x > xMax) {
				return graphFrequencyMax + 1;
			}

			double freq = graphFrequencyMax * (((double) (p.x-xMin)) / ((double) (xMax-xMin)));

			return ((double) Math.round(freq * 4)) / 4.0;

		}

		protected void setDragHighlight(double highlightStart, double highlightEnd) {

			Rectangle2D area = getScreenDataArea();

			int xMin = (int) Math.floor(area.getX());
			int xMax = (int) Math.ceil(area.getX() + area.getWidth());

			double perHz = ((double) (xMax-xMin)) / graphFrequencyMax;

			setDragHighlight((int) Math.round(xMin + highlightStart*perHz), (int) Math.round(xMin + highlightEnd*perHz));

		}

		protected void setDragHighlight(int highlightStart, int highlightEnd) {

			if (this.dragHighlightStart != highlightStart || this.dragHighlightEnd != highlightEnd) {
				this.dragHighlightStart = highlightStart;
				this.dragHighlightEnd = highlightEnd;
				repaint();
			}

		}

		protected void clearDragHighlight() {

			if (dragHighlightStart >= 0 || dragHighlightEnd >= 0) {
				dragHighlightStart = -1;
				dragHighlightEnd = -1;
				repaint();
			}

		}

		public void setSelectionHighlightStart(double highlightStart) {

			Rectangle2D area = getScreenDataArea();

			int xMin = (int) Math.floor(area.getX());
			int xMax = (int) Math.ceil(area.getX() + area.getWidth());

			double perHz = ((double) (xMax-xMin)) / graphFrequencyMax;

			setSelectionHighlight((int) Math.round(xMin + highlightStart*perHz), selectionHighlightEnd);

		}

		public void setSelectionHighlightEnd(double highlightEnd) {

			Rectangle2D area = getScreenDataArea();

			int xMin = (int) Math.floor(area.getX());
			int xMax = (int) Math.ceil(area.getX() + area.getWidth());

			double perHz = ((double) (xMax-xMin)) / graphFrequencyMax;

			setSelectionHighlight(selectionHighlightStart, (int) Math.round(xMin + highlightEnd*perHz));

		}

		protected void setSelectionHighlight(int highlightStart, int highlightEnd) {

			if (this.selectionHighlightStart != highlightStart || this.selectionHighlightEnd != highlightEnd) {
				this.selectionHighlightStart = highlightStart;
				this.selectionHighlightEnd = highlightEnd;
				repaint();
			}

		}

		public void clearSelectionHighlight() {

			if (selectionHighlightStart >= 0 || selectionHighlightEnd >= 0) {
				selectionHighlightStart = -1;
				selectionHighlightEnd = -1;
				repaint();
			}

		}

		@Override
		public void mousePressed(MouseEvent ev) {

			hideSelectionHighlight = true;
			startFrequency = getFrequency(ev.getPoint());
			if (startFrequency >= graphFrequencyMax) {
				startFrequency = null;
			}
			repaint();

		}

		@Override
		public abstract void mouseReleased(MouseEvent ev);

		@Override
		public abstract void mouseClicked(MouseEvent ev);

		@Override
		public abstract void mouseDragged(MouseEvent ev);

		@Override
		public void paintComponent(Graphics gOrig) {

			super.paintComponent(gOrig);

			Graphics2D g = (Graphics2D) gOrig;
			Rectangle2D area = getScreenDataArea();

			if (!hideSelectionHighlight) {

				if (selectionHighlightStart > 0 && selectionHighlightEnd > 0) {

					g.setColor(new Color(0.55F, 1.0F, 0.55F, 0.5F));
					g.fillRect(selectionHighlightStart, (int) area.getY(), selectionHighlightEnd-selectionHighlightStart, (int) area.getHeight());

				}

			}

			if (dragHighlightStart > 0 && dragHighlightEnd > 0) {

				g.setColor(new Color(0.5F, 0.5F, 0.5F, 0.5F));
				g.fillRect(dragHighlightStart, (int) area.getY(), dragHighlightEnd-dragHighlightStart, (int) area.getHeight());

			}

		}

	}

}
