/* MontageFilterExclusionTable.java created 2008-02-03
 *
 */

package org.signalml.app.view.montage;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableModel;

import org.signalml.app.montage.MontageFilterExclusionTableModel;
import org.signalml.app.view.element.CenteringTableCellRenderer;
import org.signalml.app.view.element.UneditableBooleanTableCellRenderer;

/** MontageFilterExclusionTable
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageFilterExclusionTable extends JTable {

	private static final long serialVersionUID = 1L;

	public static final Color DISABLED_COLOR = new Color(220,220,220);

	private static final int ROW_SIZE = 35;
	private static final int COLUMN_SIZE = 100;

	public MontageFilterExclusionTable(MontageFilterExclusionTableModel model) {

		super(model);

		setTableHeader(null);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellSelectionEnabled(true);
		setAutoResizeMode(AUTO_RESIZE_OFF);

		setRowHeight(ROW_SIZE);

		setDefaultRenderer(Boolean.class, new UneditableBooleanTableCellRenderer());

	}

	@Override
	public MontageFilterExclusionTableModel getModel() {
		return (MontageFilterExclusionTableModel) super.getModel();
	}

	@Override
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();

		TableModel model = getModel();
		if (!(model instanceof MontageFilterExclusionTableModel)) {
			return;
		}
		MontageFilterExclusionTableModel tableModel = (MontageFilterExclusionTableModel) model;

		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane)gp;
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				scrollPane.setColumnHeaderView(new ColumnHeaderTable(tableModel.getColumnTableModel()));
				scrollPane.setRowHeaderView(new RowHeaderTable(tableModel.getRowTableModel()));
				scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, new CornerPanel());
			}
		}
	}

	@Override
	protected void unconfigureEnclosingScrollPane() {
		super.unconfigureEnclosingScrollPane();
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane)gp;
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				scrollPane.setColumnHeaderView(null);
				scrollPane.setRowHeaderView(null);
				scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, null);
			}
		}
	}

	@Override
	public void columnAdded(TableColumnModelEvent e) {
		super.columnAdded(e);
		int index = e.getToIndex();
		getColumnModel().getColumn(index).setPreferredWidth(COLUMN_SIZE);
	}

	private class CornerPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		public CornerPanel() {
			super();
			setBackground(DISABLED_COLOR);
			setPreferredSize(new Dimension(COLUMN_SIZE,ROW_SIZE));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Dimension size = getSize();

			g.setColor(getGridColor());
			g.drawLine(0, size.height-1, size.width-1, size.height-1);
			g.drawLine(size.width-1, 0, size.width-1, size.height-1);
		}

	}

	private class ColumnHeaderTable extends JTable {

		private static final long serialVersionUID = 1L;

		public ColumnHeaderTable(TableModel dm) {
			super(dm);

			setTableHeader(null);
			setDefaultRenderer(String.class, new CenteringTableCellRenderer());
			setBackground(DISABLED_COLOR);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setEnabled(false);
			setAutoResizeMode(AUTO_RESIZE_OFF);

			setRowHeight(ROW_SIZE);

			setToolTipText("");
		}

		@Override
		public void columnAdded(TableColumnModelEvent e) {
			super.columnAdded(e);
			int index = e.getToIndex();
			getColumnModel().getColumn(index).setPreferredWidth(COLUMN_SIZE);
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			Point p = event.getPoint();
			int row = rowAtPoint(p);
			int col = columnAtPoint(p);
			if (row >= 0 && col >= 0) {
				return (String) getValueAt(row, col);
			} else {
				return "";
			}
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(getColumnCount()*COLUMN_SIZE, getRowCount()*ROW_SIZE);
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

	}

	private class RowHeaderTable extends JTable {

		private static final long serialVersionUID = 1L;

		public RowHeaderTable(TableModel dm) {
			super(dm);

			setTableHeader(null);
			setDefaultRenderer(String.class, new CenteringTableCellRenderer());
			setBackground(DISABLED_COLOR);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setEnabled(false);
			setAutoResizeMode(AUTO_RESIZE_OFF);

			setRowHeight(ROW_SIZE);

			setToolTipText("");

		}

		@Override
		public void columnAdded(TableColumnModelEvent e) {
			super.columnAdded(e);
			int index = e.getToIndex();
			getColumnModel().getColumn(index).setPreferredWidth(COLUMN_SIZE);
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			Point p = event.getPoint();
			int row = rowAtPoint(p);
			int col = columnAtPoint(p);
			if (row >= 0 && col >= 0) {
				return (String) getValueAt(row, col);
			} else {
				return "";
			}
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

	}

}
