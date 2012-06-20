/* UneditableBooleanTableCellRenderer.java created 2008-02-04
 *
 */

package org.signalml.app.view.components;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * Renderer for the cell with the check-box (the {@code value} has type boolean).
 * This cell has:
 * <ul>
 * <li>the border depending on the fact if the cell is focused,</li>
 * <li>the background and foreground colors depending on the fact if the
 * cell is selected.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class UneditableBooleanTableCellRenderer extends JCheckBox implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * the border used when the cell is not focused
	 */
	private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	/**
	 * Constructor.
	 * Sets that the check-box should be located in the center and that the
	 * border should be painted.
	 */
	public UneditableBooleanTableCellRenderer() {
		super();
		setHorizontalAlignment(JLabel.CENTER);
		setBorderPainted(true);
	}

	/**
	 * Returns this check-box as the component used for drawing the cell:
	 * <ul>
	 * <li>with the normal or selection colors (background and foreground)
	 * according to {@code isSelected},</li>
	 * <li>{@link #setSelected(boolean) checked} ({@code true)} or unchecked
	 * according to {@code value},</li>
	 * <li>with the border appropriate to {code hasFocus},</li>
	 * <li>enabled or disabled depending on the fact if the cell is {@link
	 * TableModel#isCellEditable(int, int) editable}.</li></ul>
	 */
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
		setEnabled(editable);

		return this;
	}

}
