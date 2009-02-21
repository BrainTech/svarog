/* ReferenceTable.java created 2007-10-24
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

import org.signalml.app.montage.ReferenceTableModel;
import org.signalml.app.view.element.CenteringTableCellRenderer;

/** ReferenceTable
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ReferenceTable extends JTable {

	private static final long serialVersionUID = 1L;

	public static final Color DISABLED_COLOR = new Color(220,220,220);
	
	private static final int CELL_SIZE = 35;
	
	public ReferenceTable(ReferenceTableModel model) {
		
		super(model);
		
		setTableHeader(null);
		setDefaultRenderer(String.class, new ReferenceTableCellRenderer());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellSelectionEnabled(true);
		setAutoResizeMode(AUTO_RESIZE_OFF);
		
		setRowHeight(CELL_SIZE);
		
		setToolTipText("");
				
	}
		
	@Override
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();

		TableModel model = getModel();
		if( !(model instanceof ReferenceTableModel) ) {
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
		getColumnModel().getColumn(index).setPreferredWidth(CELL_SIZE);
	}
	
	@Override
	public String getToolTipText(MouseEvent event) {
		Point p = event.getPoint();
		int row = rowAtPoint(p);
		int col = columnAtPoint(p);
		if( row >= 0 && col >= 0 ) {
			return (String) getValueAt(row, col);
		} else {
			return null;
		}
	}
		
	private class CornerPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		public CornerPanel() {
			super();
			setBackground(DISABLED_COLOR);
			setPreferredSize(new Dimension(CELL_SIZE,CELL_SIZE));
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
	
	private class HeaderTable extends JTable {

		private static final long serialVersionUID = 1L;

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
		
		@Override
		public String getToolTipText(MouseEvent event) {
			Point p = event.getPoint();
			int row = rowAtPoint(p);
			int col = columnAtPoint(p);
			if( row >= 0 && col >= 0 ) {
				return (String) getValueAt(row, col);
			} else {
				return null;
			}
		}
		
		@Override
		public void columnAdded(TableColumnModelEvent e) {
			super.columnAdded(e);
			int index = e.getToIndex();
			getColumnModel().getColumn(index).setPreferredWidth(CELL_SIZE);
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(getColumnCount()*CELL_SIZE, getRowCount()*CELL_SIZE);
		}
		
		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}
		
	}
	
}
