package org.signalml.app.view.components.presets;

import org.signalml.app.config.preset.Preset;
import org.signalml.plugin.export.SignalMLException;

/**
 * Interface for all views (panels or dialogs) that are capable of
 * returning a preset or other {@link ComplexPresetControlsPanel} can
 * set presets to them.
 *
 * @author Piotr Szachewicz
 */
public interface PresetableView {

	/**
	 * (Creates and) returns the current {@link Preset preset}. Must be
	 * specified in the implementing class.
	 *
	 * @return the current preset
	 * @throws SignalMLException
	 *             TODO never thrown in implementations (???)
	 */
	Preset getPreset() throws SignalMLException;

	/**
	 * Sets the given preset as the current {@link Preset preset}. Fills all
	 * necessary fields of the dialog with the data from this preset. Must be
	 * specified in the implementing class.
	 *
	 * @param preset
	 *            the preset to use as the new current preset.
	 * @return true if preset was set
	 * @throws SignalMLException
	 *             TODO never thrown in implementations
	 */
	void setPreset(Preset preset) throws SignalMLException;

	/**
	 * Checks if the preset can be set for this view.
	 * @param preset
	 * @return
	 */
	boolean isPresetCompatible(Preset preset);
}
