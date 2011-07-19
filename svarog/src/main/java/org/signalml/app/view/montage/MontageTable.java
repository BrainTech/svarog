/* MontageTable.java created 2007-09-11
 *
 */
package org.signalml.app.view.montage;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DropMode;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.signalml.app.montage.MontageTableModel;
import org.signalml.app.view.TablePopupMenuProvider;
import org.signalml.app.view.element.GrayTableCellRenderer;
import org.signalml.app.view.montage.dnd.MontageTableTransferHandler;
import org.springframework.context.support.MessageSourceAccessor;

/** MontageTable
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageTable extends JTable {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MontageTable.class);

	private TablePopupMenuProvider popupMenuProvider;

	public MontageTable(MontageTableModel model, MessageSourceAccessor messageSource, boolean simplified) {
		super(model, (TableColumnModel) null);

		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		columnModel.setColumnSelectionAllowed(false);

		TableColumn tc;

		GrayTableCellRenderer grayIneditableTableCellRenderer = new GrayTableCellRenderer();
		ChannelTableCellRenderer channelTableCellRenderer = new ChannelTableCellRenderer();
		channelTableCellRenderer.setMessageSource(messageSource);

		if (simplified) {
			tc = new TableColumn(MontageTableModel.PRIMARY_LABEL_COLUMN, 80);
			tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
			tc.setCellRenderer(grayIneditableTableCellRenderer);
			columnModel.addColumn(tc);
		} else {
			tc = new TableColumn(MontageTableModel.INDEX_COLUMN, 100);
			tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
			tc.setCellRenderer(grayIneditableTableCellRenderer);
			columnModel.addColumn(tc);

			tc = new TableColumn(MontageTableModel.PRIMARY_LABEL_COLUMN, 200);
			tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
			columnModel.addColumn(tc);
		}

		tc = new TableColumn(MontageTableModel.LABEL_COLUMN, 200);
		tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
		columnModel.addColumn(tc);

		setColumnModel(columnModel);

		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e) && (e.getClickCount() == 1)) {
					int index = rowAtPoint(e.getPoint());
					ListSelectionModel selectionModel = getSelectionModel();
					if (!selectionModel.isSelectedIndex(index)) {
						selectionModel.setSelectionInterval(index, index);
					}
				}
			}

		});

		getTableHeader().setReorderingAllowed(false);

		if (!simplified) {
			setTransferHandler(new MontageTableTransferHandler());
			setDragEnabled(true);
			setDropMode(DropMode.INSERT_ROWS);
			setFillsViewportHeight(true);
		}

	}

	@Override
	public MontageTableModel getModel() {
		return (MontageTableModel) super.getModel();
	}

	@Override
	public JPopupMenu getComponentPopupMenu() {
		if (popupMenuProvider == null) {
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
