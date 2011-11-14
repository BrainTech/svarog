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
import org.signalml.domain.montage.MontageChannel;
import org.signalml.domain.montage.SourceChannel;

/**
 * The table which allows to edit the labels and the order (the indexes) of
 * {@link MontageChannel montage channels}.
 * This table has 3 columns:
 * <ul>
 * <li>the column with the index of the {@link MontageChannel montage
 * channel}; the column is gray and ineditable,</li>
 * <li>the column with the label of the {@link SourceChannel source channel}
 * which is the base for the motage channel; the column is gray and
 * ineditable,</li>
 * <li>the column with the label of the montage channel (default type),</li>
 * </ul>
 * Multiple rows of this table can be selected but the columns
 * can not be selected at all.
 * <p>
 * The order (and the indexes) of the channels can be changed with
 * drag and drop.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageTable extends JTable {

	/**
	 * the default serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the logger
	 */
	protected static final Logger logger = Logger.getLogger(MontageTable.class);

	/**
	 * the {@link TablePopupMenuProvider popup menu provider} for this table
	 */
	private TablePopupMenuProvider popupMenuProvider;

	/**
	 * Creates the table with 3 columns:
	 * <ul>
	 * <li>the column with the index of the {@link MontageChannel montage
	 * channel}; the column is gray and ineditable,</li>
	 * <li>the column with the label of the {@link SourceChannel source channel}
	 * which is the base for the motage channel; the column is gray and
	 * ineditable,</li>
	 * <li>the column with the label of the montage channel (default type),</li>
	 * </ul>
	 * Multiple rows of this table can be selected but the columns
	 * can not be selected at all.
	 * <p>
	 * The order (and the indexes) of the channels can be changed with
	 * drag and drop.
	 * @param model the {@link MontageTableModel model} for this table
	 * @param simplified <code>true</code> if the index column should be
	 * omitted, <code>false</code> otherwise
	 */
	public MontageTable(MontageTableModel model, boolean simplified) {
		super(model, (TableColumnModel) null);

		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		columnModel.setColumnSelectionAllowed(false);

		TableColumn tc;

		GrayTableCellRenderer grayIneditableTableCellRenderer = new GrayTableCellRenderer();

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

	/* (non-Javadoc)
	 * @see javax.swing.JTable#getModel()
	 */
	@Override
	public MontageTableModel getModel() {
		return (MontageTableModel) super.getModel();
	}

	/**
	 * Returns a {@link TablePopupMenuProvider#getPopupMenu(int, int) popup
	 * menu} for a selected row from {@link #popupMenuProvider}.
	 */
	@Override
	public JPopupMenu getComponentPopupMenu() {
		if (popupMenuProvider == null) {
			return null;
		}
		return popupMenuProvider.getPopupMenu(-1, getSelectedRow());
	}

	/**
	 * Gets the {@link TablePopupMenuProvider popup menu provider} for this
	 * table.
	 * 
	 * @return the {@link TablePopupMenuProvider popup menu provider} for this
	 *         table
	 */
	public TablePopupMenuProvider getPopupMenuProvider() {
		return popupMenuProvider;
	}

	/**
	 * Sets the {@link TablePopupMenuProvider popup menu provider} for this
	 * table.
	 * 
	 * @param popupMenuProvider
	 *            the new {@link TablePopupMenuProvider popup menu provider} for
	 *            this table
	 */
	public void setPopupMenuProvider(TablePopupMenuProvider popupMenuProvider) {
		this.popupMenuProvider = popupMenuProvider;
	}

}
