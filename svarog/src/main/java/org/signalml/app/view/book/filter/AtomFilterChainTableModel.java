/* AtomFilterChainTableModel.java created 2008-03-04
 *
 */

package org.signalml.app.view.book.filter;

import static org.signalml.app.SvarogApplication._;
import javax.swing.table.AbstractTableModel;

import org.signalml.domain.book.filter.AtomFilterChain;
import org.signalml.exception.SanityCheckException;
import org.springframework.context.MessageSourceResolvable;

/** AtomFilterChainTableModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class AtomFilterChainTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	public static final int INDEX_COLUMN = 0;
	public static final int NAME_COLUMN = 1;
	public static final int TYPE_COLUMN = 2;
	public static final int BLOCKING_COLUMN = 3;
	public static final int ENABLED_COLUMN = 4;

	private AtomFilterChain chain;

	public  AtomFilterChainTableModel() {
	}

	public AtomFilterChain getChain() {
		return chain;
	}

	public void setChain(AtomFilterChain chain) {
		if (this.chain != chain) {
			this.chain = chain;
			fireTableDataChanged();
		}
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {

		case INDEX_COLUMN :
				return Integer.class;

		case NAME_COLUMN :
			return String.class;

		case TYPE_COLUMN :
			return MessageSourceResolvable.class;

		case BLOCKING_COLUMN :
			return Boolean.class;

		case ENABLED_COLUMN :
			return Boolean.class;

		default :
			throw new SanityCheckException("Unsupported index [" + columnIndex + "]");

		}
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {

		case INDEX_COLUMN :
			return _("Index");

		case NAME_COLUMN :
			return _("Name");

		case TYPE_COLUMN :
			return _("Type");

		case BLOCKING_COLUMN :
			return _("Blocking");

		case ENABLED_COLUMN :
			return _("Enabled");

		default :
			throw new SanityCheckException("Unsupported index [" + column + "]");

		}
	}

	@Override
	public int getRowCount() {
		if (chain == null) {
			return 0;
		}
		return chain.getFilterCount();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == BLOCKING_COLUMN || columnIndex == ENABLED_COLUMN) {
			return true;
		}
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		switch (columnIndex) {

		case INDEX_COLUMN :
			return new Integer(rowIndex+1);

		case NAME_COLUMN :
			return chain.getFilterAt(rowIndex).getName();

		case TYPE_COLUMN :
			return chain.getFilterAt(rowIndex);

		case BLOCKING_COLUMN :
			return new Boolean(chain.getFilterAt(rowIndex).isBlocking());

		case ENABLED_COLUMN :
			return new Boolean(chain.getFilterAt(rowIndex).isEnabled());

		default :
			throw new SanityCheckException("Unsupported index [" + columnIndex + "]");

		}

	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {

		switch (columnIndex) {

		case BLOCKING_COLUMN :
			chain.getFilterAt(rowIndex).setBlocking((Boolean) value);
			break;

		case ENABLED_COLUMN :
			chain.getFilterAt(rowIndex).setEnabled((Boolean) value);
			break;

		default :
			throw new SanityCheckException("Unsupported index [" + columnIndex + "]");

		}

	}

	// TODO rework into an event driven model if ever have time

	public void onUpdate() {
		fireTableDataChanged();
	}

	public void onUpdate(int row) {
		fireTableRowsUpdated(row, row);
	}

	public void onInsert(int row) {
		fireTableRowsInserted(row, row);
	}

	public void onDelete(int row) {
		fireTableRowsDeleted(row, row);
	}

}
