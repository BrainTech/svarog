/* FFTSampleFilterTable.java created 2008-02-03
 *
 */
package org.signalml.app.view.montage.filters;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.signalml.app.model.montage.FFTSampleFilterTableModel;
import org.signalml.app.view.TablePopupMenuProvider;

/** FFTSampleFilterTable
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class FFTSampleFilterTable extends JTable {

	private static final long serialVersionUID = 1L;

	private TablePopupMenuProvider popupMenuProvider;

	public FFTSampleFilterTable(FFTSampleFilterTableModel model) {
		super(model, (TableColumnModel) null);

		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		columnModel.setColumnSelectionAllowed(false);

		TableColumn tc;

		tc = new TableColumn(FFTSampleFilterTableModel.FREQUENCY_COLUMN, 200);
		tc.setHeaderValue(model.getColumnName(tc.getModelIndex()));
		columnModel.addColumn(tc);

		tc = new TableColumn(FFTSampleFilterTableModel.COEFFICIENT_COLUMN, 100);
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

	@Override
	public FFTSampleFilterTableModel getModel() {
		return (FFTSampleFilterTableModel) super.getModel();
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
