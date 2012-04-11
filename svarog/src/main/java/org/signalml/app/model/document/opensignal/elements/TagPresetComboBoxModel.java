package org.signalml.app.model.document.opensignal.elements;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManagerEvent;
import org.signalml.app.config.preset.PresetManagerListener;
import org.signalml.app.config.preset.StyledTagSetPresetManager;

/**
 * A model for handling a combo box containing the list of available tag style
 * presets.
 *
 * It is available to add an empty preset option to the list through
 * appropriate calling {@link TagPresetComboBoxModel#setShowEmptyOption(boolean)}.
 * Currently it shows the empty option by default.
 *
 * @author Piotr Szachewicz
 */
public class TagPresetComboBoxModel extends AbstractListModel implements ComboBoxModel, PresetManagerListener {

	/**
	 * The {@link PresetManager} handling tag style presets.
	 */
	private final StyledTagSetPresetManager styledTagSetPresetManager;
	/**
	 * Decides whether a special empty preset should be added to the preset list.
	 */
	private boolean showEmptyOption = true;
	/**
	 * The currently selected preset.
	 */
	private Object selectedOption;

	/**
	 * Constructor.
	 * @param styledTagSetPresetManager the {@link PresetManager} from which
	 * the model's data will taken.
	 */
	public TagPresetComboBoxModel(StyledTagSetPresetManager styledTagSetPresetManager) {
		this.styledTagSetPresetManager = styledTagSetPresetManager;
		styledTagSetPresetManager.addPresetManagerListener(this);
	}

	/**
	 * Sets whether a special empty option should be added to the preset list
	 * (a preset containing no styles).
	 * @param showEmptyOption true, if an empty preset should be added, false
	 * otherwise
	 */
	public void setShowEmptyOption(boolean showEmptyOption) {
		this.showEmptyOption = showEmptyOption;
	}

	/**
	 * Returns the options available on the list.
	 * @return the options available
	 */
	protected Preset[] getAvailableOptions() {
		if (showEmptyOption) {
			return styledTagSetPresetManager.getPresetsWithEmptyOption();
		} else {
			return styledTagSetPresetManager.getPresets();
		}
	}

	@Override
	public int getSize() {
		return getAvailableOptions().length;
	}

	@Override
	public Object getElementAt(int index) {
		return getAvailableOptions()[index];
	}

	@Override
	public void setSelectedItem(Object anItem) {
		selectedOption = anItem;
		refreshList();
	}

	@Override
	public Object getSelectedItem() {
		return selectedOption;
	}

	@Override
	public void presetAdded(PresetManagerEvent ev) {
		refreshList();
	}

	@Override
	public void presetRemoved(PresetManagerEvent ev) {
		if (ev.getOldPreset() == selectedOption) {
			selectedOption = null;
		}
		refreshList();
	}

	@Override
	public void presetReplaced(PresetManagerEvent ev) {
		if (ev.getOldPreset() == selectedOption) {
			selectedOption = ev.getNewPreset();
		}
		refreshList();
	}

	@Override
	public void defaultPresetChanged(PresetManagerEvent ev) {
		refreshList();
	}

	/**
	 * Informs all listeners that the model has changed.
	 */
	protected void refreshList() {
		fireContentsChanged(this, -1, -1);
	}

}
