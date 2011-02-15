/* EditSampleFilterDialog.java created 2010-09-22
 *
 */
package org.signalml.app.view.montage.filters;

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
	private float samplingFrequency;

	/**
	 * A {@link JTextField} which can be used to edit the filter's description.
	 */
	private JTextField descriptionTextField;

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
	public JPanel getChartGroupPanelWithABorder() {

		/*JPanel chartGroupPanel = new JPanel(new BorderLayout(6, 6));

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("editSampleFilter.graphPanelTitle")),
			new EmptyBorder(3, 3, 3, 3));
		chartGroupPanel.setBorder(border);

		chartGroupPanel.add(getChartGroupPanel());*/

		return getChartGroupPanel();

	}

	/**
	 * Returns a JPanel containing a group of charts (or maybe just one graph)
	 * to be shown in this dialog.
	 * @return a group of charts with a maximum graph scale spinner.
	 */
	public abstract JPanel getChartGroupPanel();

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
	 * Returns the sampling frequency for which the filter is being designed.
	 * @return the sampling frequency for the currently edited filter
	 */
	public float getCurrentSamplingFrequency() {
		return samplingFrequency;
	}

	/**
	 * Sets the sampling frequency for which this filter will be designed.
	 * @param currentSamplingFrequency the sampling frequency for the
	 * currently edited filter
	 */
	public void setCurrentSamplingFrequency(float currentSamplingFrequency) {
		this.samplingFrequency = currentSamplingFrequency;
	}

	/**
	 * Returns the maximum frequency which can be set for these filters.
	 * @return maximum frequency for all controls in this dialog
	 */
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

	/**
	 * Gets a double value from a spinner.
	 * @param spinner the spinner from which the value should be taken
	 * @return
	 */
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

}
