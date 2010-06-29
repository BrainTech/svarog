/* GrayIneditableTableCellRenderer.java created 2007-10-24
 *
 */

package org.signalml.app.view.element;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/** GrayIneditableTableCellRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class GrayIneditableTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private static final Color DISABLED_COLOR = new Color(220,220,220);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (!isSelected) {
			if (table.getModel().isCellEditable(row, column)) {
				label.setBackground(table.getBackground());
			} else {
				label.setBackground(DISABLED_COLOR);
			}
		} else {
			label.setBackground(table.getSelectionBackground());
		}
		return label;
	}

}
