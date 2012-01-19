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

import org.signalml.app.model.montage.MontageFilterExclusionTableModel;
import org.signalml.app.view.components.CenteringTableCellRenderer;
import org.signalml.app.view.components.UneditableBooleanTableCellRenderer;
import org.signalml.domain.montage.MontageChannel;
import org.signalml.domain.montage.MontageSampleFilter;

/**
 * The table which allows to check which {@link MontageChannel montage channels}
 * should not be {@link MontageSampleFilter filtered} by which filter.
 * This table has: 
 * <ul>
 * <li>no header,</li>
 * <li>{@link ListSelectionModel#SINGLE_SELECTION single selection}
 * mode,</li>
 * <li>{@link #ROW_SIZE} height of the row,</li>
 * <li>{@link #COLUMN_SIZE} width of the column,</li>
 * <li>{@link RowHeaderTable} at the headers of rows,</li>
 * <li>{@link ColumnHeaderTable} at the headers of columns,</li>
 * <li>{@link CornerPanel} at the upper left corner,</li>
 * <li>{@link MontageFilterExclusionTableModel} as the model.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageFilterExclusionTable extends JTable {

	/**
	 * the default serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the color used if the cell is not editable
	 */
	public static final Color DISABLED_COLOR = new Color(220,220,220);

	/**
	 * the height of the row
	 */
	private static final int ROW_SIZE = 35;
	
	/**
	 * the width of the column
	 */
	private static final int COLUMN_SIZE = 100;

	/**
	 * Constructor. Creates the table with the given
	 * {@link MontageFilterExclusionTableModel model}, single selection mode
	 * and {@link #ROW_SIZE specified} row height.
	 * @param model the model for this table
	 */
	public MontageFilterExclusionTable(MontageFilterExclusionTableModel model) {

		super(model);

		setTableHeader(null);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellSelectionEnabled(true);
		setAutoResizeMode(AUTO_RESIZE_OFF);

		setRowHeight(ROW_SIZE);

		setDefaultRenderer(Boolean.class, new UneditableBooleanTableCellRenderer());

	}

	/**
	 * @return the {@link MontageFilterExclusionTableModel model} for this
	 * table
	 */
	@Override
	public MontageFilterExclusionTableModel getModel() {
		return (MontageFilterExclusionTableModel) super.getModel();
	}

	/**
	 * Does almost the same as {@link JTable#configureEnclosingScrollPane()
	 * parent method} but:
	 * <ul>
	 * <li>sets {@link RowHeaderTable} as a row header view,</li>
	 * <li>sets {@link ColumnHeaderTable} as a column header view,</li>
	 * <li>sets the {@link CornerPanel} in the
	 * {@link ScrollPaneConstants#UPPER_LEFT_CORNER upper left corner}.</li>
	 * </ul>
	 */
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

	/**
	 * Does almost the same as {@link JTable#configureEnclosingScrollPane()
	 * parent method} but:
	 * <ul>
	 * <li>sets the row and column headers to {@code null},</li>
	 * <li>sets the corner in
	 * {@link ScrollPaneConstants#UPPER_LEFT_CORNER upper left corner}
	 * to {@code null}.</li>
	 * </ul>
	 */
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

	/**
	 * Does the same as {@link JTable#columnAdded(TableColumnModelEvent)} and
	 * sets the preferred width of the cell to {@link
	 * MontageFilterExclusionTable#COLUMN_SIZE}.
	 */
	@Override
	public void columnAdded(TableColumnModelEvent e) {
		super.columnAdded(e);
		int index = e.getToIndex();
		getColumnModel().getColumn(index).setPreferredWidth(COLUMN_SIZE);
	}

	/**
	 * The panel used at the corner of the {@link MontageFilterExclusionTable}.
	 * It is a square with {@link MontageFilterExclusionTable#DISABLED_COLOR}
	 * background and two lines that separate it from other cells.
	 */
	private class CornerPanel extends JPanel {

		/**
		 * the default serialization constant
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets:
		 * <ul><li> the background color to {@link
		 * MontageFilterExclusionTable#DISABLED_COLOR},</li>
		 * <li>the width to {@link MontageFilterExclusionTable#ROW_SIZE} and
		 * the height to {@link MontageFilterExclusionTable#COLUMN_SIZE}.
		 * </li></ul> 
		 */
		public CornerPanel() {
			super();
			setBackground(DISABLED_COLOR);
			setPreferredSize(new Dimension(COLUMN_SIZE,ROW_SIZE));
		}

		/**
		 * {@link JPanel#paintComponents(Graphics) Paints} this component
		 * and two lines that separate it from other cells.
		 */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Dimension size = getSize();

			g.setColor(getGridColor());
			g.drawLine(0, size.height-1, size.width-1, size.height-1);
			g.drawLine(size.width-1, 0, size.width-1, size.height-1);
		}

	}

	/**
	 * The table used at the column headers in {@link MontageFilterExclusionTable}.
	 * It has:
	 * <ul>
	 * <li>{@link MontageFilterExclusionTable#DISABLED_COLOR} as background
	 * color,</li>
	 * <li>{@link ListSelectionModel#SINGLE_SELECTION single selection}
	 * mode,</li>
	 * <li>{@link MontageFilterExclusionTable#ROW_SIZE ROW_SIZE} as the row
	 * height,</li>
	 * <li>{@link MontageFilterExclusionTable#COLUMN_SIZE COLUMN_SIZE}
	 * as the column width,</li>
	 * <li>the values centered in the cell.</li></ul> 
	 */
	private class ColumnHeaderTable extends JTable {

		
		/**
		 * the default serialization constant
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets:
		 * <ul>
		 * <li>{@link MontageFilterExclusionTable#DISABLED_COLOR} as background
		 * color,</li>
		 * <li>{@link ListSelectionModel#SINGLE_SELECTION single selection}
		 * mode,</li>
		 * <li>that the values are centered,</li>
		 * <li>the row height to {@link MontageFilterExclusionTable#ROW_SIZE
		 * ROW_SIZE} and no autoresize.</li>
		 * </ul>
		 * @param dm the data model for the table
		 */
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

		/**
		 * Does the same as {@link JTable#columnAdded(TableColumnModelEvent)}
		 * and sets the preferred width of the cell to {@link
		 * MontageFilterExclusionTable#COLUMN_SIZE}.
		 */
		@Override
		public void columnAdded(TableColumnModelEvent e) {
			super.columnAdded(e);
			int index = e.getToIndex();
			getColumnModel().getColumn(index).setPreferredWidth(COLUMN_SIZE);
		}

		/**
		 * Returns the value of the cell (the name of the column or the row)
		 * as the tooltip text.
		 */
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

		/**
		 * Returns the preferred size of this table as:
		 * {@link MontageFilterExclusionTable#COLUMN_SIZE COLUMN_SIZE}{@code
		 * *column_count x}{@link MontageFilterExclusionTable#ROW_SIZE}{@code
		 * *row_count}
		 */
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(getColumnCount()*COLUMN_SIZE, getRowCount()*ROW_SIZE);
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

	}

	/**
	 * The table used at the row headers in {@link MontageFilterExclusionTable}.
	 * It has:
	 * <ul>
	 * <li>{@link MontageFilterExclusionTable#DISABLED_COLOR} as background
	 * color,</li>
	 * <li>{@link ListSelectionModel#SINGLE_SELECTION single selection}
	 * mode,</li>
	 * <li>{@link MontageFilterExclusionTable#ROW_SIZE ROW_SIZE} as the row
	 * height,</li>
	 * <li>{@link MontageFilterExclusionTable#COLUMN_SIZE COLUMN_SIZE}
	 * as the column width,</li>
	 * <li>the values centered in the cell.</li></ul> 
	 */
	private class RowHeaderTable extends JTable {

		/**
		 * the default serialization constant
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets:
		 * <ul>
		 * <li>{@link MontageFilterExclusionTable#DISABLED_COLOR} as background
		 * color,</li>
		 * <li>{@link ListSelectionModel#SINGLE_SELECTION single selection}
		 * mode,</li>
		 * <li>that the values are centered,</li>
		 * <li>the row height to {@link MontageFilterExclusionTable#ROW_SIZE
		 * ROW_SIZE} and no autoresize.</li>
		 * </ul>
		 * @param dm the data model for the table
		 */
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

		/**
		 * Does the same as {@link JTable#columnAdded(TableColumnModelEvent)}
		 * and sets the preferred width of the cell to {@link
		 * MontageFilterExclusionTable#COLUMN_SIZE}.
		 */
		@Override
		public void columnAdded(TableColumnModelEvent e) {
			super.columnAdded(e);
			int index = e.getToIndex();
			getColumnModel().getColumn(index).setPreferredWidth(COLUMN_SIZE);
		}

		/**
		 * Returns the value of the cell (the name of the column or the row)
		 * as the tooltip text.
		 */
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
