/* ReferenceTable.java created 2007-10-24
 *
 */

package org.signalml.app.view.montage;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableModel;

import org.signalml.app.model.montage.ReferenceTableModel;
import org.signalml.app.view.common.components.cellrenderers.CenteringTableCellRenderer;
import org.signalml.domain.montage.MontageChannel;

/**
 * The table which displays the reference between {@link MontageChannel montage
 * channels} and original channels.
 * This table has:
 * <ul>
 * <li>no header,</li>
 * <li>{@link ListSelectionModel#SINGLE_SELECTION single selection}
 * mode,</li>
 * <li>{@link #CELL_SIZE} height and width of the cells,</li>
 * <li>{@link HeaderTable} at the headers of rows and columns,</li>
 * <li>{@link CornerPanel} at the upper left corner,</li>
 * <li>{@link ReferenceTableModel} as the model.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ReferenceTable extends JTable implements ActionListener {

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
	private static final int CELL_SIZE = 35;

	/**
	 * Constructor. Creates the table:
	 * <ul>
	 * <li>without header,</li>
	 * <li>with the {@link ListSelectionModel#SINGLE_SELECTION single selection}
	 * mode,</li>
	 * <li>with the {@link #CELL_SIZE} height of the row,</li>
	 * <li>without autoresize.</li></ul>
	 * @param model data {@link ReferenceTableModel model} for this table
	 */
	public ReferenceTable(ReferenceTableModel model) {

		super(model);

		setTableHeader(null);
		setDefaultRenderer(String.class, new ReferenceTableCellRenderer());
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setCellSelectionEnabled(true);
		setAutoResizeMode(AUTO_RESIZE_OFF);

		setRowHeight(CELL_SIZE);

		setToolTipText("");

		//hook on 'paste' action
		KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false);
		this.registerKeyboardAction(this,"Paste",paste,JComponent.WHEN_FOCUSED);
	}

	/**
	 * Does almost the same as {@link JTable#configureEnclosingScrollPane()
	 * parent method} but:
	 * <ul>
	 * <li>sets {@link HeaderTable} as a column and row header view,</li>
	 * <li>sets the {@link CornerPanel} in the
	 * {@link ScrollPaneConstants#UPPER_LEFT_CORNER upper left corner}.</li>
	 * </ul>
	 */
	@Override
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();

		TableModel model = getModel();
		if (!(model instanceof ReferenceTableModel)) {
			return;
		}
		ReferenceTableModel referenceTableModel = (ReferenceTableModel) model;

		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane)gp;
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				scrollPane.setColumnHeaderView(new HeaderTable(referenceTableModel.getColumnTableModel()));
				scrollPane.setRowHeaderView(new HeaderTable(referenceTableModel.getRowTableModel()));
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
	 * sets the preferred width of the cell to {@link #CELL_SIZE}.
	 */
	@Override
	public void columnAdded(TableColumnModelEvent e) {
		super.columnAdded(e);
		int index = e.getToIndex();
		getColumnModel().getColumn(index).setPreferredWidth(CELL_SIZE);
	}

	/**
	 * Returns the value of the cell as the text for the tool-tip.
	 */
	@Override
	public String getToolTipText(MouseEvent event) {
		Point p = event.getPoint();
		int row = rowAtPoint(p);
		int col = columnAtPoint(p);
		if (row >= 0 && col >= 0) {
			return (String) getValueAt(row, col);
		} else {
			return null;
		}
	}

	/**
	 * The panel used at the corner of the {@link ReferenceTable}.
	 * It is a square with {@link ReferenceTable#DISABLED_COLOR} background
	 * and two lines that separate it from other cells.
	 */
	private class CornerPanel extends JPanel {

		/**
		 * the default serialization constant
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets:
		 * <ul><li> the background color to {@link
		 * ReferenceTable#DISABLED_COLOR},</li>
		 * <li>the width and height both to {@link ReferenceTable#CELL_SIZE}.
		 * </li></ul>
		 */
		public CornerPanel() {
			super();
			setBackground(DISABLED_COLOR);
			setPreferredSize(new Dimension(CELL_SIZE,CELL_SIZE));
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
	 * The table used at the row and column headers in {@link ReferenceTable}.
	 * It has:
	 * <ul>
	 * <li>{@link ReferenceTable#DISABLED_COLOR} as background color,</li>
	 * <li>{@link ListSelectionModel#SINGLE_SELECTION single selection}
	 * mode,</li>
	 * <li>{@link ReferenceTable#CELL_SIZE CELL_SIZE} as the row height and
	 * width,</li>
	 * <li>the values centered in the cell.</li></ul>
	 */
	private class HeaderTable extends JTable {

		/**
		 * the default serialization constant
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets:
		 * <ul>
		 * <li>{@link ReferenceTable#DISABLED_COLOR} as background color,</li>
		 * <li>{@link ListSelectionModel#SINGLE_SELECTION single selection}
		 * mode,</li>
		 * <li>that the values are centered,</li>
		 * <li>the row height to {@link ReferenceTable#CELL_SIZE} and no
		 * autoresize.</li>
		 * </ul>
		 * @param dm the data model for the table
		 */
		public HeaderTable(TableModel dm) {
			super(dm);

			setTableHeader(null);
			setDefaultRenderer(String.class, new CenteringTableCellRenderer());
			setBackground(DISABLED_COLOR);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setEnabled(false);
			setAutoResizeMode(AUTO_RESIZE_OFF);

			setRowHeight(CELL_SIZE);

			setToolTipText("");
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
				return null;
			}
		}

		/**
		 * Does the same as {@link JTable#columnAdded(TableColumnModelEvent)} and
		 * sets the preferred width of the cell to {@link
		 * ReferenceTable#CELL_SIZE}.
		 */
		@Override
		public void columnAdded(TableColumnModelEvent e) {
			super.columnAdded(e);
			int index = e.getToIndex();
			getColumnModel().getColumn(index).setPreferredWidth(CELL_SIZE);
		}

		/**
		 * Returns the preferred size of this table as:
		 * {@link ReferenceTable#CELL_SIZE CELL_SIZE}{@code
		 * *column_count x CELL_SIZE*row_count}
		 */
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(getColumnCount()*CELL_SIZE, getRowCount()*CELL_SIZE);
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		//If current action is 'paste' then copy string from the clipboard and paste it to current cell
		if (evt.getActionCommand().compareTo("Paste")==0) {
			Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
			String trstring="";
			try {
				trstring = (String)(system.getContents(this).getTransferData(DataFlavor.stringFlavor));
			} catch (UnsupportedFlavorException e) {
				return;
			} catch (IOException e) {
				return;
			}
			int[] cols = this.getSelectedColumns();
			int[] rows = this.getSelectedRows();
			for (int i = 0; i < rows.length; i++)
				for (int j = 0; j < cols.length; j++)
					this.setValueAt(trstring, rows[i], cols[j]);
		}
	}

}
