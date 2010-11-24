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
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** EditSampleFilterDialog
 *
 *
 * @author Piotr Szachewicz
 */
abstract class EditSampleFilterDialog extends AbstractPresetDialog {

	private static final long serialVersionUID = 1L;

	private float currentSamplingFrequency;

	private JTextField descriptionTextField;

	protected double graphFrequencyMax;
	protected JSpinner graphScaleSpinner;
	protected XYPlot frequencyResponsePlot;
	protected JFreeChart frequencyResponseChart;
	protected FrequencyResponseChartPanel frequencyResponseChartPanel;

	protected NumberAxis frequencyAxis;
	protected NumberAxis gainAxis;

	public EditSampleFilterDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w, boolean isModal) {
		super(messageSource, presetManager, w, isModal);
	}

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

	public JTextField getDescriptionTextField() {

		if (descriptionTextField == null) {
			descriptionTextField = new JTextField();
			descriptionTextField.setPreferredSize(new Dimension(200, 25));
		}
		return descriptionTextField;

	}

	public NumberAxis getFrequencyAxis() {

		if (frequencyAxis == null) {

			frequencyAxis = new NumberAxis();
			frequencyAxis.setAutoRange(false);
			frequencyAxis.setLabel(messageSource.getMessage("editSampleFilter.graphFrequencyLabel"));

		}
		return frequencyAxis;

	}

	public abstract NumberAxis getGainAxis();

	public XYPlot getFrequencyResponsePlot() {

		if (frequencyResponsePlot == null) {

			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);

			frequencyResponsePlot = new XYPlot(null, getFrequencyAxis(), getGainAxis(), renderer);

		}
		return frequencyResponsePlot;

	}

	public JFreeChart getFrequencyResponseChart() {

		if (frequencyResponseChart == null) {
			frequencyResponseChart = new JFreeChart(messageSource.getMessage("editSampleFilter.graphTitle"), new Font(Font.DIALOG, Font.PLAIN, 12), getFrequencyResponsePlot(), false);
			frequencyResponseChart.setBorderVisible(true);
			frequencyResponseChart.setBackgroundPaint(Color.WHITE);
			frequencyResponseChart.setPadding(new RectangleInsets(5, 5, 5, 5));
		}
		return frequencyResponseChart;

	}

	public abstract FrequencyResponseChartPanel getFrequencyResponseChartPanel();

	public JSpinner getGraphScaleSpinner() {

		if (graphScaleSpinner == null) {
			graphScaleSpinner = new JSpinner(new SpinnerNumberModel(0.25, 0.25, 4096.0, 0.25));
			graphScaleSpinner.setPreferredSize(new Dimension(80, 25));

			graphScaleSpinner.setEditor(new JSpinner.NumberEditor(graphScaleSpinner, "0.00"));
			graphScaleSpinner.setFont(graphScaleSpinner.getFont().deriveFont(Font.PLAIN));
		}
		return graphScaleSpinner;

	}

	public float getCurrentSamplingFrequency() {
		return currentSamplingFrequency;
	}

	public void setCurrentSamplingFrequency(float currentSamplingFrequency) {
		this.currentSamplingFrequency = currentSamplingFrequency;
	}

	public double getGraphFrequencyMax() {
		return graphFrequencyMax;
	}

	public void setGraphFrequencyMax(double graphFrequencyMax) {

		if (this.graphFrequencyMax != graphFrequencyMax) {

			this.graphFrequencyMax = graphFrequencyMax;

			getGraphScaleSpinner().setValue(graphFrequencyMax);

		}

	}

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
