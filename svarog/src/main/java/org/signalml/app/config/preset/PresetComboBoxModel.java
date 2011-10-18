/* PresetComboBoxModel.java created 2007-10-24
 *
 */

package org.signalml.app.config.preset;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/** PresetComboBoxModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PresetComboBoxModel extends AbstractListModel implements ComboBoxModel, PresetManagerListener {

	private static final long serialVersionUID = 1L;

	private PresetManager presetManager;
	private String chooseTitle;

	private Object selectedItem;

	public PresetComboBoxModel(String chooseTitle, PresetManager presetManager) {
		super();
		this.chooseTitle = chooseTitle;
		this.presetManager = presetManager;
		this.presetManager.addPresetManagerListener(this);
	}

	protected boolean isChooseTitleShowed() {
		if (chooseTitle == null)
			return false;
		return true;
	}

	@Override
	public int getSize() {
		int size = presetManager.getPresetCount();
		if (isChooseTitleShowed())
			size += 1;
		return size;
	}

	@Override
	public Object getElementAt(int index) {
		if (isChooseTitleShowed()) {
			if (index == 0)
				return chooseTitle;
			else
				return presetManager.getPresetAt(index-1);
		}
		else
			return presetManager.getPresetAt(index);
	}

	@Override
	public Object getSelectedItem() {
		return selectedItem;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		selectedItem = anItem;
	}

	@Override
	public void defaultPresetChanged(PresetManagerEvent ev) {
		// ignored
	}

	@Override
	public void presetAdded(PresetManagerEvent ev) {
		fireContentsChanged(this, 1, getSize());
	}

	@Override
	public void presetRemoved(PresetManagerEvent ev) {
		fireContentsChanged(this, 1, getSize());
	}

	@Override
	public void presetReplaced(PresetManagerEvent ev) {
		fireContentsChanged(this, 1, getSize());
	}

}
