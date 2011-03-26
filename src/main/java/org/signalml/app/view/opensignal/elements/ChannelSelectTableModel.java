/* ChannelSelectTableModel.java created 2011-03-23
 *
 */

package org.signalml.app.view.opensignal.elements;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Piotr Szachewicz
 */
public class ChannelSelectTableModel extends AbstractTableModel {

	private AmplifierChannels channels = new AmplifierChannels();

	public static final int SELECTED_COLUMN = 0;
	public static final int NUMBER_COLUMN = 1;
	public static final int NAME_COLUMN = 2;

	public ChannelSelectTableModel() {
	}

	public void setChannels(AmplifierChannels channels) {
		this.channels = channels;
		fireTableDataChanged();
	}

	public AmplifierChannels getAmplifierChannels() {
		return channels;
	}

	@Override
	public int getRowCount() {
		return channels.getNumberOfChannels();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		AmplifierChannel channel = channels.getChannel(rowIndex);
		switch(columnIndex) {
			case SELECTED_COLUMN: return channel.isSelected();
			case NUMBER_COLUMN: return channel.getNumber();
			case NAME_COLUMN: return channel.getName();
			default: return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0 || columnIndex == 2)
			return true;
		else
			return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		AmplifierChannel x = channels.getChannel(rowIndex);
		if (columnIndex == 0)
			x.setSelected((Boolean) aValue);
		else if (columnIndex == 2)
			x.setName(aValue.toString());

		fireTableCellUpdated(rowIndex, columnIndex);
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
			case SELECTED_COLUMN: return "";
			case NUMBER_COLUMN: return "number";
			case NAME_COLUMN: return "label";
			default: return "";
		}
	}

	public void setAllSelected(boolean selected) {
		channels.setAllSelected(selected);
		fireTableDataChanged();
	}
}
