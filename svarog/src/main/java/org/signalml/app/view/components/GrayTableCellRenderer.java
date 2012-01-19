/* GrayTableCellRenderer.java created 2007-10-24
 *
 */

package org.signalml.app.view.components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Table cell renderer which can have two different background colors:
 * <ul>
 * <li>background color of the table if the cell is selected,</li>
 * <li>{@link #DISABLED_COLOR} otherwise).</li></ul>
 * @see GrayIneditableTableCellRenderer
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class GrayTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * the color which is used when a cell is not selected
	 */
	private static final Color DISABLED_COLOR = new Color(220,220,220);

	/**
	 * Returns the label {@link DefaultTableCellRenderer#getTableCellRendererComponent(
	 * JTable, Object, boolean, boolean, int, int) obtained} from {@link
	 * DefaultTableCellRenderer parent} with the changed background:
	 * <ul>
	 * <li>background color of the table if the cell is selected,</li>
	 * <li>{@link #DISABLED_COLOR} otherwise).</li></ul>
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (!isSelected) {
			label.setBackground(DISABLED_COLOR);
		} else {
			label.setBackground(table.getSelectionBackground());
		}
		return label;
	}

}
