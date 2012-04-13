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

/**
 * Table cell renderer for the {@link ReferenceTable} which can have three
 * different background colors:
 * <ul>
 * <li>{@link #FILLED_COLOR} if the cell is editable and there is a value
 * in it,</li>
 * <li>the {@link JTable#getBackground() default background} for the table
 * if the cell is editable and empty,</li>
 * <li>{@link #DISABLED_COLOR} if the cell is not editable.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ReferenceTableCellRenderer extends DefaultTableCellRenderer {

	/**
	 * the default serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the color used if the cell is not editable
	 */
	private static final Color DISABLED_COLOR = new Color(220,220,220);

	/**
	 * the color used if the cell is editable and there is a value in it
	 */
	private static final Color FILLED_COLOR = new Color(255,255,153);

	/**
	 * Constructor. Sets that the text in the cell is centered both horizontally
	 * and vertically.
	 */
	public ReferenceTableCellRenderer() {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
	}

	/**
	 * Returns the label {@link DefaultTableCellRenderer#getTableCellRendererComponent(
	 * JTable, Object, boolean, boolean, int, int) obtained} from {@link
	 * DefaultTableCellRenderer parent} with the changed background:
	 * <ul>
	 * <li>{@link #FILLED_COLOR} if the cell is editable and there is a value
	 * in it,</li>
	 * <li>the {@link JTable#getBackground() default background} for the table
	 * if the cell is editable and empty,</li>
	 * <li>{@link #DISABLED_COLOR} if the cell is not editable.</li></ul>
	 */
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
