/* SleepComparisonTable.java created 2008-03-03
 * 
 */

package org.signalml.app.method.stager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.signalml.app.model.TableToTextExporter;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.element.CenteringTableCellRenderer;
import org.springframework.context.support.MessageSourceAccessor;

/** SleepComparisonTable
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SleepComparisonTable extends JTable {

	protected static final Logger logger = Logger.getLogger(SleepComparisonTable.class);
	
	private static final long serialVersionUID = 1L;

	public static final Color DISABLED_COLOR = new Color(220,220,220);
	
	private static final int CELL_SIZE = 62;
	
	private TableToTextExporter tableToTextExporter;
	private ViewerFileChooser fileChooser;
	
//	private MessageSourceAccessor messageSource;
//	private JPopupMenu popupMenu;
	
	private CornerPanel cornerPanel;
	
	// TODO comments & messageSource argument - needed?
	public SleepComparisonTable(SleepComparisonTableModel model, MessageSourceAccessor messageSource) {
				
		super(model);
//		this.messageSource = messageSource;
		
		setTableHeader(null);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);		
		setDefaultRenderer(String.class, new CenteringTableCellRenderer());
		setDefaultRenderer(Integer.class, new CenteringTableCellRenderer());
		setCellSelectionEnabled(true);
		setAutoResizeMode(AUTO_RESIZE_OFF);
		
		setRowHeight(CELL_SIZE);
				
		setToolTipText("");
				
	}
		
	public TableToTextExporter getTableToTextExporter() {
		return tableToTextExporter;
	}

	public void setTableToTextExporter(TableToTextExporter tableToTextExporter) {
		this.tableToTextExporter = tableToTextExporter;
	}
	
	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	@Override
	public SleepComparisonTableModel getModel() {
		return (SleepComparisonTableModel) super.getModel();
	}
	
	@Override
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();

		TableModel model = getModel();
		if( !(model instanceof SleepComparisonTableModel) ) {
			return;
		}
		SleepComparisonTableModel tableModel = (SleepComparisonTableModel) model;
		
        Container p = getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane)gp;
                JViewport viewport = scrollPane.getViewport();
                if (viewport == null || viewport.getView() != this) {
                    return;
                }
                scrollPane.setColumnHeaderView(new HeaderTable(tableModel.getColumnTableModel()));
                scrollPane.setRowHeaderView(new HeaderTable(tableModel.getRowTableModel()));
                scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, getCornerPanel());
            }
        }
	}
		
	private CornerPanel getCornerPanel() {
		if( cornerPanel == null ) {
			cornerPanel = new CornerPanel();
		}
		return cornerPanel;
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
			return ((Integer) getValueAt(row, col)).toString();
		} else {
			return null;
		}
	}
		
	/*
	@Override
	public JPopupMenu getComponentPopupMenu() {
		if( popupMenu == null ) {
			popupMenu = new JPopupMenu();
			popupMenu.add( new ExportComparisonToClipboardAction(messageSource, tableToTextExporter) );
			popupMenu.add( new ExportComparisonToFileAction(messageSource, tableToTextExporter) );
		}
		return popupMenu;
	}
	
	protected class ExportComparisonToClipboardAction extends ExportToClipboardAction {

		private static final long serialVersionUID = 1L;

		public ExportComparisonToClipboardAction(MessageSourceAccessor messageSource, TableToTextExporter tableToTextExporter) {
			super(messageSource, tableToTextExporter);
		}

		@Override
		protected Object getUserObject() {
			TagComparisonTableModel model = getModel();
			if( model != null ) {
				return model.isShowPercent();
			}
			return null;
		}
		
		@Override
		protected WriterExportableTable getExportableTable() {
			TagComparisonTableModel model = getModel();
			if( model != null ) {
				return model.getResult();
			}
			return null;
		}
		
	}

	protected class ExportComparisonToFileAction extends ExportToFileAction {

		private static final long serialVersionUID = 1L;
								
		private ExportComparisonToFileAction(MessageSourceAccessor messageSource, TableToTextExporter tableToTextExporter) {
			super(messageSource, tableToTextExporter);
			setFileChooser(fileChooser);
			setOptionPaneParent(SleepComparisonTable.this);
		}
			
		@Override
		protected Object getUserObject() {
			TagComparisonTableModel model = getModel();
			if( model != null ) {
				return model.isShowPercent();
			}
			return null;
		}
		
		@Override
		protected WriterExportableTable getExportableTable() {
			TagComparisonTableModel model = getModel();
			if( model != null ) {
				return model.getResult();
			}
			return null;
		}
		
	}
	*/
	
	private class CornerPanel extends JPanel {

		private static final long serialVersionUID = 1L;

//		private JLabel label;
		
		public CornerPanel() {
			super( new BorderLayout() );
			setBackground(DISABLED_COLOR);
			setPreferredSize(new Dimension(CELL_SIZE,CELL_SIZE));
			
			/*
			label = new JLabel();
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setVerticalAlignment(JLabel.CENTER);
			
			add( label, BorderLayout.CENTER );
			*/
			
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			Dimension size = getSize();
			
			g.setColor(getGridColor());
			g.drawLine(0, size.height-1, size.width-1, size.height-1);
			g.drawLine(size.width-1, 0, size.width-1, size.height-1);
		}

		/*
		public JLabel getLabel() {
			return label;
		}

		@Override
		public JPopupMenu getComponentPopupMenu() {
			return SleepComparisonTable.this.getComponentPopupMenu();
		}
		*/
		
	}
	
	private class HeaderTable extends JTable {

		private static final long serialVersionUID = 1L;

		public HeaderTable(TableModel dm) {
			super(dm);

			setTableHeader(null);
			setBackground(DISABLED_COLOR);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setDefaultRenderer(String.class, new CenteringTableCellRenderer());
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
				Object value = getValueAt(row, col);
				if( value == null ) {
					return null;
				}
				if( value instanceof String ) {
					return (String) value;
				} 
			}
			return null;
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
		
		@Override
		public JPopupMenu getComponentPopupMenu() {
			return SleepComparisonTable.this.getComponentPopupMenu();
		}
		
	}
		
}
