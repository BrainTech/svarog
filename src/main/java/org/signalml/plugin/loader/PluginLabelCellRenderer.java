/**
 * 
 */
package org.signalml.plugin.loader;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 * This class represents a renderer for cells containing 
 * labels in the {@link PluginTableModel table} of
 * plug-in states.
 * If plug-in has some missing dependencies or loading
 * of this plug-in failed the cell has red background 
 * and the tooltip is set. 
 * 
 * @author Marcin Szumski
 */
public class PluginLabelCellRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	
	/**
	 * an array of plug-in {@link PluginState states}
	 */
	ArrayList<PluginState> descriptions;
	
	/**
	 * Constructor.
	 * @param descriptions an array of plug-in {@link PluginState states}
	 */
	public PluginLabelCellRenderer(ArrayList<PluginState> descriptions){
		this.descriptions = descriptions;
		setOpaque(true);
	}
	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		PluginState state = descriptions.get(row);
		if (!state.getMissingDependencies().isEmpty() || state.isFailedToLoad()){
			setBackground(Color.RED);
			String message = new String();
			if (state.isFailedToLoad()){
				message += "failed to load";
			} else {
				message += "missing dependencies: ";
				message += descriptions.get(row).missingDependenciesToString();
			}
			setToolTipText(message);
		} else {
			setBackground(Color.WHITE);
			setToolTipText("");
		}
		if (column == 1) setHorizontalAlignment(SwingConstants.CENTER);
		String text = (String) value;
		setText(text);
		return this;
	}

}
