/* AtomFilterChainTable.java created 2008-03-04
 * 
 */
package org.signalml.app.view.book.filter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableColumnModel;

import org.signalml.app.view.TablePopupMenuProvider;
import org.signalml.app.view.element.ResolvableTableCellRenderer;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

/** AtomFilterChainTable
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class AtomFilterChainTable extends JTable {

	private static final long serialVersionUID = 1L;

	private TablePopupMenuProvider popupMenuProvider;
		
	public AtomFilterChainTable(AtomFilterChainTableModel model, MessageSourceAccessor messageSource) {
		
		super(model, (TableColumnModel) null);
				
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		addMouseListener( new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				if( SwingUtilities.isRightMouseButton(e) && (e.getClickCount() == 1) ) {
					int index = rowAtPoint(e.getPoint());
					ListSelectionModel selectionModel = getSelectionModel();
					if( !selectionModel.isSelectedIndex(index) ) {
						selectionModel.setSelectionInterval(index, index);
					}
				}
			}
			
		});
		
		setDefaultRenderer(MessageSourceResolvable.class, new ResolvableTableCellRenderer(messageSource));
		
		getTableHeader().setReorderingAllowed(false);
				
		TableColumnModel columnModel = getColumnModel();
		columnModel.setColumnSelectionAllowed(false);		
		
	}
			
	@Override
	public void columnAdded(TableColumnModelEvent e) {
		super.columnAdded(e);
		int index = e.getToIndex();
		int width;
		
		switch( index ) {
		
		case AtomFilterChainTableModel.INDEX_COLUMN :
			width = 50;
			break;
			
		case AtomFilterChainTableModel.BLOCKING_COLUMN :		
			width = 40;
			break;

		case AtomFilterChainTableModel.ENABLED_COLUMN :		
			width = 40;
			break;
		
		case AtomFilterChainTableModel.NAME_COLUMN :		
		case AtomFilterChainTableModel.TYPE_COLUMN :		
		default :
			width = 200;
			break;
			
		}

		getColumnModel().getColumn(index).setPreferredWidth(width);
		
	}
	
	@Override
	public JPopupMenu getComponentPopupMenu() {
		if( popupMenuProvider == null ) {
			return null;
		}		
		return popupMenuProvider.getPopupMenu(-1, getSelectedRow());
	}
	
	public TablePopupMenuProvider getPopupMenuProvider() {
		return popupMenuProvider;
	}

	public void setPopupMenuProvider(TablePopupMenuProvider popupMenuProvider) {
		this.popupMenuProvider = popupMenuProvider;
	}
	
}
