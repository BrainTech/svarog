package org.signalml.app.montage;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import org.signalml.domain.montage.system.EegElectrode;
import org.signalml.domain.montage.system.EegSystem;

/**
 *
 * @author Piotr Szachewicz
 */
public class ChannelsListModel extends AbstractListModel implements ComboBoxModel {

	private EegSystem eegSystem;
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
	
	public void setEegSystem(EegSystem eegSystem) {
		if (this.eegSystem == eegSystem)
			return;
		
		this.eegSystem = eegSystem;
		fireContentsChanged(this, 0, getSize()-1);
	}

}
