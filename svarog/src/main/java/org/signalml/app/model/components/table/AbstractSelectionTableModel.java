package org.signalml.app.model.components.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public abstract class AbstractSelectionTableModel<T extends Object> extends AbstractTableModel {

	public static final int CHECKBOX_COLUMN_NUMBER = 0;
	public static final int ELEMENT_NAME_COLUMN_NUMBER = 1;

	protected List<Boolean> selectionStatus = new ArrayList<Boolean>();
	protected List<T> elements = new ArrayList<T>();

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == getCheckboxColumnNumber())
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return elements.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == CHECKBOX_COLUMN_NUMBER)
			return selectionStatus.get(rowIndex);
		else
			return elements.get(rowIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return getCheckboxColumnNumber() == columnIndex;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == CHECKBOX_COLUMN_NUMBER) {
			selectionStatus.set(rowIndex, (Boolean) aValue);
		}
	}

	protected int getCheckboxColumnNumber() {
		return CHECKBOX_COLUMN_NUMBER;
	}

	/**
	 * Sets all channels in this table model to be selected/unselected.
	 * @param selected the new state of all channels in this table model
	 */
	public void setAllSelected(boolean selected) {
		for (int i = 0; i < selectionStatus.size(); i++)
			selectionStatus.set(i, selected);
		fireTableDataChanged();
	}

	public List<T> getSelectedElements() {
		List<T> selectedElements = new ArrayList<T>();

		for (int i = 0; i < selectionStatus.size(); i++)
			if (selectionStatus.get(i)) {
				selectedElements.add(elements.get(i));
			}

		return selectedElements;
	}

}
