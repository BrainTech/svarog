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
import org.signalml.domain.montage.SourceChannel;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * The table which allows to edit the labels and functions of
 * {@link SourceChannel source channels}.
 * This table has 3 columns:
 * <ul>
 * <li>the column with the index of the channel, which is gray and
 * ineditable,</li>
 * <li>the column with the label of the channel (default type),</li>
 * <li>the column with the function of the channel, for which each cell is
 * a {@link ChannelComboBox combo box} with possible functions of the
 * channel.</li></ul>
 * Multiple rows of this table can be selected but the columns
 * can not be selected at all.
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageTable extends JTable {

	/**
	 * the default serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the {@link TablePopupMenuProvider popup menu provider} for this table
	 */
	private TablePopupMenuProvider popupMenuProvider;

	/**
	 * Creates the table with 3 columns:
	 * <ul>
	 * <li>the column with the index of the channel, which is gray and
	 * ineditable,</li>
	 * <li>the column with the label of the channel (default type),</li>
	 * <li>the column with the function of the channel, for which each cell is
	 * a {@link ChannelComboBox combo box} with possible functions of the
	 * channel.</li></ul>
	 * Multiple rows of this table can be selected but the columns
	 * can not be selected at all.
	 * @param model the {@link SourceMontageTableModel model} for this table
	 * @param messageSource the source of messages (labels)
	 */
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
		channelComboBox.setModel(model.getChannelListModel());
		DefaultCellEditor channelCellEditor = new DefaultCellEditor(channelComboBox);
		channelCellEditor.setClickCountToStart(2);
		tc.setCellEditor(channelCellEditor);
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

		setTransferHandler(new SourceMontageTableTransferHandler());
		setDragEnabled(true);
		setFillsViewportHeight(true);

	}

	/* (non-Javadoc)
	 * @see javax.swing.JTable#getModel()
	 */
	@Override
	public SourceMontageTableModel getModel() {
		return (SourceMontageTableModel) super.getModel();
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
