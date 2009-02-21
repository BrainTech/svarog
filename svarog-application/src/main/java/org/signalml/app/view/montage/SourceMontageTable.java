/* SourceMontageTable.java created 2007-11-24
 * 
 */
package org.signalml.app.view.montage;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.signalml.app.montage.SourceMontageTableModel;
import org.signalml.app.view.TablePopupMenuProvider;
import org.signalml.app.view.element.ChannelComboBox;
import org.signalml.app.view.element.GrayTableCellRenderer;
import org.signalml.app.view.montage.dnd.SourceMontageTableTransferHandler;
import org.springframework.context.support.MessageSourceAccessor;

/** SourceMontageTable
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageTable extends JTable {

	private static final long serialVersionUID = 1L;

	private TablePopupMenuProvider popupMenuProvider;
		
	public SourceMontageTable(SourceMontageTableModel model, MessageSourceAccessor messageSource) {
		super(model, (TableColumnModel) null);
		
		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();		
		columnModel.setColumnSelectionAllowed(false);
		
		TableColumn tc;
		
		GrayTableCellRenderer grayIneditableTableCellRenderer = new GrayTableCellRenderer();
		ChannelTableCellRenderer channelTableCellRenderer = new ChannelTableCellRenderer();
		channelTableCellRenderer.setMessageSource(messageSource);
		
		tc = new TableColumn(SourceMontageTableModel.INDEX_COLUMN, 100);
		tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
		tc.setCellRenderer(grayIneditableTableCellRenderer);
		columnModel.addColumn(tc);
				
		tc = new TableColumn(SourceMontageTableModel.LABEL_COLUMN, 200);
		tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
		columnModel.addColumn(tc);
		
		tc = new TableColumn(SourceMontageTableModel.FUNCTION_COLUMN, 200);		
		tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
		tc.setCellRenderer(channelTableCellRenderer);
		ChannelComboBox channelComboBox = new ChannelComboBox(messageSource);
		channelComboBox.setModel( model.getChannelListModel() );
		DefaultCellEditor channelCellEditor = new DefaultCellEditor(channelComboBox);
		channelCellEditor.setClickCountToStart(2);
		tc.setCellEditor(channelCellEditor);
		columnModel.addColumn(tc);
		
		setColumnModel(columnModel);		
		
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

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
				
		setTransferHandler( new SourceMontageTableTransferHandler() );
		setDragEnabled(true);
		setFillsViewportHeight(true);
		
	}
		
	@Override
	public SourceMontageTableModel getModel() {
		return (SourceMontageTableModel) super.getModel();
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
