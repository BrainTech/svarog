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
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.filter.SampleFilterDefinition;

/**
 * Table with the list of {@link SampleFilterDefinition sample filters}
 * associated with a {@link Montage}.
 * Contains three columns:
 * <ul>
 * <li>the index of the filter in the montage,</li> 
 * <li>the custom name of the filter (description),</li>
 * <li>the description of the effect of the filter.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageFiltersTable extends JTable {

	/** the default serialization constant. */
	private static final long serialVersionUID = 1L;

	/** the logger. */
	protected static final Logger logger = Logger.getLogger(MontageFiltersTable.class);

	/** the popup menu provider for this table. */
	private TablePopupMenuProvider popupMenuProvider;

	/**
	 * Creates the table with three columns:
	 * <ul>
	 * <li>the index of the filter in the montage (20 pixel),</li>
	 * <li>the custom name of the filter (200 pixel),</li>
	 * <li>the description of the effect of the filter (200 pixel).</li></ul>
	 * Adds the mouse listener which changes the selected row, when it is
	 * clicked with a right mouse button.
	 * @param model the model for this table
	 * @param messageSource the source of messages (labels)
	 */
	public MontageFiltersTable(MontageFiltersTableModel model) {
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

	}

	/* (non-Javadoc)
	 * @see javax.swing.JTable#getModel()
	 */
	@Override
	public MontageFiltersTableModel getModel() {
		return (MontageFiltersTableModel) super.getModel();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getComponentPopupMenu()
	 */
	@Override
	public JPopupMenu getComponentPopupMenu() {
		if (popupMenuProvider == null) {
			return null;
		}
		return popupMenuProvider.getPopupMenu(-1, getSelectedRow());
	}

	/**
	 * Gets the popup menu provider for this table.
	 *
	 * @return the popup menu provider for this table
	 */
	public TablePopupMenuProvider getPopupMenuProvider() {
		return popupMenuProvider;
	}

	/**
	 * Sets the popup menu provider for this table.
	 *
	 * @param popupMenuProvider the new popup menu provider for this table
	 */
	public void setPopupMenuProvider(TablePopupMenuProvider popupMenuProvider) {
		this.popupMenuProvider = popupMenuProvider;
	}

}
