package org.signalml.app.view.opensignal;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManagerEvent;
import org.signalml.app.config.preset.PresetManagerListener;
import org.signalml.app.config.preset.StyledTagSetPresetManager;

/**
 *
 * @author Piotr Szachewicz
 */
public class TagPresetComboBoxModel extends AbstractListModel implements ComboBoxModel, PresetManagerListener {

	private final StyledTagSetPresetManager styledTagSetPresetManager;
	private boolean showEmptyOption = true;
	private Object selectedOption;

	public TagPresetComboBoxModel(StyledTagSetPresetManager styledTagSetPresetManager) {
		this.styledTagSetPresetManager = styledTagSetPresetManager;
		styledTagSetPresetManager.addPresetManagerListener(this);
	}

	public void setShowEmptyOption(boolean showEmptyOption) {
		this.showEmptyOption = showEmptyOption;
	}

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

	protected void refreshList() {
		fireContentsChanged(this, -1, -1);
	}

}
