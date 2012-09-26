/**
 *
 */
package org.signalml.plugin.loader;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * The model for the table containing the information which plug-ins are
 * active/should be active at the next start of the application.
 * Each row stands for one plug-in and has 3 cells: name, version and the state
 * (active/inactive) of the plug-in.
 * <p>
 * If the plug-in has no missing {@link PluginDependency dependencies} and
 * there were no error while loading it, the third cell (in the third column)
 * is editable and allows to select if the plug-in should be active at
 * the next start of the application.
 * Otherwise this cell can not be edited and the whole row has a red background.
 * Also the tool-tip with the description of the problem is set.
 *
 * @author Marcin Szumski
 */
public class PluginTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	/**
	 * the names of columns
	 */
	private String[] columnNames = new String[] {
		"Name", "version", "active"
	};
	/**
	 * an array in which the data are stored.
	 * first index - row numbers,
	 * second index - column numbers.
	 */
	private Object[][] data;

	/**
	 * an array of plug-in {@link PluginState states}
	 */
	private ArrayList<PluginState> descriptions;

	/**
	 * Constructor.
	 * @param descriptions an array of plug-in {@link PluginState states}
	 */
	public PluginTableModel(ArrayList<PluginState> descriptions) {
		data = new Object[descriptions.size()][];
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}

	@Override
	public Class getColumnClass(int col) {
		Class clazz = data[0][col].getClass();
		return clazz;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 2) {
			PluginState state = descriptions.get(rowIndex);
			if (state.getMissingDependencies().isEmpty() && !state.isFailedToLoad()) return true;
		}

		return false;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		data[rowIndex][columnIndex] = value;
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	/**
	 * Updates the plug-in states from users input.
	 * @param descriptions the states to be updated
	 */
	public void fillModel(ArrayList<PluginState> descriptions) {
		for (int i = 0; i < data.length ; ++i) {
			boolean active = ((Boolean) data[i][2]).booleanValue();
			descriptions.get(i).setActive(active);
		}
	}

	/**
	 * Fills the table with data from provided array.
	 * @param descriptions an array of plug-in {@link PluginState states}
	 */
	public void fromModel(ArrayList<PluginState> descriptions) {
		for (int i = 0; i < descriptions.size(); ++i) {
			PluginState description = descriptions.get(i);
			if (data[i] == null) data[i] = new Object[3];
			data[i][0] = description.getName();
			data[i][1] = description.versionToString();
			data[i][2] = description.isActive();
		}
		this.descriptions = descriptions;
	}


}
