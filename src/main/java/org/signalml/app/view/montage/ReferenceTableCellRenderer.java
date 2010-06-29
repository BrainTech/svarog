/* ReferenceTableCellRenderer.java created 2007-11-24
 *
 */

package org.signalml.app.view.montage;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/** ReferenceTableCellRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ReferenceTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private static final Color DISABLED_COLOR = new Color(220,220,220);
	private static final Color FILLED_COLOR = new Color(255,255,153);

	public ReferenceTableCellRenderer() {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (!isSelected) {
			if (table.getModel().isCellEditable(row, column)) {
				if (value != null && !((String) value).isEmpty()) {
					label.setBackground(FILLED_COLOR);
				} else {
					label.setBackground(table.getBackground());
				}
			} else {
				label.setBackground(DISABLED_COLOR);
			}
		} else {
			label.setBackground(table.getSelectionBackground());
		}
		return label;
	}

}
