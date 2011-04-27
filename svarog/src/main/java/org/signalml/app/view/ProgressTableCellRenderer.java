/* ProgressTableCellRenderer.java created 2007-10-06
 *
 */

package org.signalml.app.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import org.signalml.task.AggregateTaskProgressInfo;

/** ProgressTableCellRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ProgressTableCellRenderer extends JPanel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	private JProgressBar progressBar;
	private EmptyBorder noFocusBorder;
	private CompoundBorder focusBorder = null;

	public ProgressTableCellRenderer() {
		super(new BorderLayout());
		noFocusBorder = new EmptyBorder(4,4,4,4);
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		add(progressBar);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if (isSelected) {
			setBackground(table.getSelectionBackground());
		} else {
			setBackground(table.getBackground());
		}

		if (hasFocus) {
			if (focusBorder == null) {
				Border border = null;
				if (isSelected) {
					border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
				}
				if (border == null) {
					border = UIManager.getBorder("Table.focusCellHighlightBorder");
				}
				Insets ins = border.getBorderInsets(this);
				focusBorder = new CompoundBorder(
				        border,
				        new EmptyBorder(4-ins.top,4-ins.left,4-ins.bottom,4-ins.right)
				);
			}
			setBorder(focusBorder);
		} else {
			setBorder(noFocusBorder);
		}

		AggregateTaskProgressInfo progressInfo = (AggregateTaskProgressInfo) value;
		progressBar.setMaximum(progressInfo.getMaxValue());
		progressBar.setValue(progressInfo.getValue());

		return this;

	}

}
