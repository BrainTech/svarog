/* EditSampleFilterDialog.java created 2010-09-22
 *
 */
package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
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
	 * A {@link JPanel} containing filter-related plots.
	 */
	protected JPanel filterGraphsPanel;
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
			new EmptyBorder(3, 3, 3, 3));
		descriptionPanel.setBorder(border);

		descriptionPanel.add(getDescriptionTextField());

		return descriptionPanel;

	}

	/**
	 * Returns the {@link JPanel} containing the filter response plot
	 * (or plots), maximum graph frequency spinner etc. surrounded by
	 * a labeled border.
	 * @return the {@link JPanel} containing a fiter response
	 * graph (graphs)
	 */
	public JPanel getGraphPanel() {

		JPanel graphPanel = new JPanel(new BorderLayout(6, 6));

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("editSampleFilter.graphPanelTitle")),
			new EmptyBorder(3, 3, 3, 3));
		graphPanel.setBorder(border);

		graphPanel.add(getGraphsPanel());

		return graphPanel;

	}

	public abstract JPanel getGraphsPanel();

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

	protected double getMaximumFrequency() {
		return getCurrentSamplingFrequency() / 2;
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

	protected double getSpinnerDoubleValue(JSpinner spinner) {
		return ((Number) spinner.getValue()).doubleValue();
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
		} else if (!Util.validateString(description)) {
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
}
