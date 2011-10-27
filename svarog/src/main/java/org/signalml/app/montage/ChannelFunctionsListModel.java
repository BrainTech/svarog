package org.signalml.app.montage;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import org.signalml.domain.montage.system.ChannelFunction;

/**
 * A ListModel that handles showing and selecting the list of available
 * {@link ChannelFunction ChannelFunctions}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelFunctionsListModel extends AbstractListModel implements ComboBoxModel {

	private static final long serialVersionUID = 1L;
	private Object selectedItem;

	public ChannelFunctionsListModel() {
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
	public int getSize() {
		return ChannelFunction.values().length;
	}

	@Override
	public Object getElementAt(int index) {
		return ChannelFunction.values()[index];
	}

}
