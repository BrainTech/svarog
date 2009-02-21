/* UneditableBooleanTableCellRenderer.java created 2008-02-04
 * 
 */

package org.signalml.app.view.element;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/** UneditableBooleanTableCellRenderer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class UneditableBooleanTableCellRenderer extends JCheckBox implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	public UneditableBooleanTableCellRenderer() {
		super();
		setHorizontalAlignment(JLabel.CENTER);
		setBorderPainted(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if (isSelected) {
			setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		setSelected((value != null && ((Boolean) value).booleanValue()));

		if (hasFocus) {
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
		} else {
			setBorder(noFocusBorder);
		}
		
		boolean editable = table.getModel().isCellEditable(row, column);
		setEnabled( editable );

		return this;
	}

}
