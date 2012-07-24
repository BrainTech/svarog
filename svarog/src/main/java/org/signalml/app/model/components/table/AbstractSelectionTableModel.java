package org.signalml.app.model.components.table;

import javax.swing.table.AbstractTableModel;

public abstract class AbstractSelectionTableModel extends AbstractTableModel {

	protected abstract int getCheckboxColumnNumber();

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == getCheckboxColumnNumber())
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}

	/**
	 * Sets all channels in this table model to be selected/unselected.
	 * @param selected the new state of all channels in this table model
	 */
	public abstract void setAllSelected(boolean selected);
}
