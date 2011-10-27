package org.signalml.app.montage;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import org.signalml.domain.montage.system.EegSystem;

/**
 * A ListModel which handles showing and selecting the list of available
 * channel labels for the current EEG system.
 *
 * @author Piotr Szachewicz
 */
public class ChannelsListModel extends AbstractListModel implements ComboBoxModel {

	/**
	 * The {@link EegSystem} for which the list of channel labels is shown.
	 */
	private EegSystem eegSystem;
	/**
	 * The item currently selected on the list.
	 */
	private Object selectedItem;

	@Override
	public int getSize() {
		if (eegSystem == null)
			return 0;
		return eegSystem.getNumberOfElectrodes();
	}

	@Override
	public Object getElementAt(int index) {
		if (eegSystem != null)
			return eegSystem.getElectrodeAt(index).toString();
		else
			return null;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		selectedItem = anItem;
		fireContentsChanged(anItem, -1, getSize());
	}

	@Override
	public Object getSelectedItem() {
		return selectedItem;
	}

	/**
	 * Sets the {@link EegSystem} for which the list of channels lables
	 * will be shown.
	 * @param eegSystem the new {@link EegSystem} for this ListModel.
	 */
	public void setEegSystem(EegSystem eegSystem) {
		if (this.eegSystem == eegSystem)
			return;

		this.eegSystem = eegSystem;
		fireContentsChanged(this, 0, getSize()-1);
	}

}
