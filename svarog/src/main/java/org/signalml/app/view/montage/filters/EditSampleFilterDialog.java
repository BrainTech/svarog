/* EditSampleFilterDialog.java created 2010-09-22
 *
 */
package org.signalml.app.view.montage.filters;

import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.common.dialogs.AbstractPresetDialog;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.plugin.export.SignalMLException;

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
	 * Constructor. Sets the message source, parent window, preset manager
	 * for time domain filters and if this dialog blocks top-level windows.
	 * @param presetManager a {@link PresetManager} to manage the presets
	 * configured in this window
	 * @param w the parent window or null if there is no parent
	 * @param isModal true if this dialog should block top-level windows,
	 * false otherwise
	 */
	public EditSampleFilterDialog(PresetManager presetManager, Window w, boolean isModal) {
		super(presetManager, w, isModal);
	}

	/**
	 * Constructor. Sets the message source and a preset manager
	 * for this window.
	 * @param presetManager a {@link PresetManager} to manage the presets
	 * configured in this window
	 */
	public EditSampleFilterDialog(PresetManager presetManager) {
		super(presetManager);
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
	 * Returns the {@link JPanel} containing the filter response plot
	 * (or plots), maximum graph frequency spinner etc. surrounded by
	 * a labeled border.
	 * @return a group of charts with a maximum graph scale spinner.
	 */
	public abstract JPanel getChartGroupPanelWithABorder();

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
	 *
	 * @return whether the graph was updated successfully.
	 */
	protected abstract boolean updateGraph();

	/**
	 * Updates the rectangle which highlights the selected frequency range.
	 */
	protected abstract void updateHighlights();

	@Override
	public abstract void fillDialogFromModel(Object model) throws SignalMLException;

	@Override
	public abstract void fillModelFromDialog(Object model) throws SignalMLException;

	@Override
	public abstract boolean supportsModelClass(Class<?> clazz);

}
