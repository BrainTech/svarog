/* MontageFiltersTable.java created 2008-02-03
 * 
 */
package org.signalml.app.view.montage;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.signalml.app.montage.MontageFiltersTableModel;
import org.signalml.app.view.TablePopupMenuProvider;
import org.springframework.context.support.MessageSourceAccessor;

/** MontageFiltersTable
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageFiltersTable extends JTable {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MontageFiltersTable.class);
	
	private TablePopupMenuProvider popupMenuProvider;
			
	public MontageFiltersTable(MontageFiltersTableModel model, MessageSourceAccessor messageSource) {
		super(model, (TableColumnModel) null);
		
		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();		
		columnModel.setColumnSelectionAllowed(false);
		
		TableColumn tc;
				
		tc = new TableColumn(MontageFiltersTableModel.INDEX_COLUMN, 40);
		tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
		columnModel.addColumn(tc);
				
		tc = new TableColumn(MontageFiltersTableModel.DESCRIPTION_COLUMN, 200);
		tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
		columnModel.addColumn(tc);
		
		tc = new TableColumn(MontageFiltersTableModel.EFFECT_COLUMN, 200);
		tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
		columnModel.addColumn(tc);
		
		setColumnModel(columnModel);		
		
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
		
		getTableHeader().setReorderingAllowed(false);
						
	}
	
	@Override
	public MontageFiltersTableModel getModel() {
		return (MontageFiltersTableModel) super.getModel();
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
