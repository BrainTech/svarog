/* MontageFilterExclusionTableModel.java created 2008-02-03
 *
 */

package org.signalml.app.model.montage;

import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageEvent;
import org.signalml.domain.montage.MontageListener;
import org.signalml.domain.montage.MontageSampleFilterEvent;
import org.signalml.domain.montage.MontageSampleFilterListener;

/** MontageFilterExclusionTableModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageFilterEnableTableModel extends AbstractTableModel implements MontageListener, MontageSampleFilterListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MontageFilterEnableTableModel.class);

	private Montage montage;

	private ColumnTableModel columnTableModel;
	private RowTableModel rowTableModel;

	public MontageFilterEnableTableModel() {
	}

	public ColumnTableModel getColumnTableModel() {
		if (columnTableModel == null) {
			columnTableModel = new ColumnTableModel();
		}
		return columnTableModel;
	}

	public RowTableModel getRowTableModel() {
		if (rowTableModel == null) {
			rowTableModel = new RowTableModel();
		}
		return rowTableModel;
	}

	private void reset() {
		fireTableStructureChanged();
		if (columnTableModel != null) {
			columnTableModel.fireTableStructureChanged();
		}
		if (rowTableModel != null) {
			rowTableModel.fireTableStructureChanged();
		}
	}

	public Montage getMontage() {
		return montage;
	}

	public void setMontage(Montage montage) {
		if (this.montage != montage) {
			if (this.montage != null) {
				this.montage.removeMontageListener(this);
				this.montage.removeMontageSampleFilterListener(this);
			}
			this.montage = montage;
			if (montage != null) {
				montage.addMontageListener(this);
				montage.addMontageSampleFilterListener(this);
			}
			reset();
		}
	}

	@Override
	public void montageChannelsAdded(MontageEvent ev) {
		fireTableDataChanged();
		if (rowTableModel != null) {
			rowTableModel.fireTableDataChanged();
		}
	}

	@Override
	public void montageChannelsChanged(MontageEvent ev) {
		if (rowTableModel != null) {
			rowTableModel.fireTableDataChanged();
		}
	}

	@Override
	public void montageChannelsRemoved(MontageEvent ev) {
		fireTableDataChanged();
		if (rowTableModel != null) {
			rowTableModel.fireTableDataChanged();
		}
	}

	@Override
	public void montageReferenceChanged(MontageEvent ev) {
		// ignored
	}

	@Override
	public void montageStructureChanged(MontageEvent ev) {
		fireTableDataChanged();
		if (rowTableModel != null) {
			rowTableModel.fireTableDataChanged();
		}
	}

	@Override
	public void filterAdded(MontageSampleFilterEvent ev) {
		fireTableStructureChanged();
		if (columnTableModel != null) {
			columnTableModel.fireTableStructureChanged();
		}
	}

	@Override
	public void filterChanged(MontageSampleFilterEvent ev) {
		if (columnTableModel != null) {
			columnTableModel.fireTableDataChanged();
		}
	}

	@Override
	public void filterExclusionChanged(MontageSampleFilterEvent ev) {
		fireTableDataChanged();
	}

	@Override
	public void filterRemoved(MontageSampleFilterEvent ev) {
		fireTableStructureChanged();
		if (columnTableModel != null) {
			columnTableModel.fireTableStructureChanged();
		}
	}

	@Override
	public void filtersChanged(MontageSampleFilterEvent ev) {
		fireTableStructureChanged();
		if (columnTableModel != null) {
			columnTableModel.fireTableStructureChanged();
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Boolean.class;
	}

	@Override
	public int getColumnCount() {
		if (montage == null) {
			return 0;
		}
		return montage.getSampleFilterCount()+1;
	}

	@Override
	public int getRowCount() {
		if (montage == null) {
			return 0;
		}
		return montage.getMontageChannelCount()+1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			if (rowIndex == 0) {
				return null;
			}
			return !montage.isExcludeAllFilters(rowIndex-1);
		}
		else if (rowIndex == 0) {
			return montage.isFilterEnabled(columnIndex-1);
		} else {
			return !montage.isFilteringExcluded(columnIndex-1, rowIndex-1);
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {

		if (columnIndex == 0) {

			if (rowIndex == 0) {
				return;
			}
			montage.setExcludeAllFilters(rowIndex-1, !((Boolean) value));

		}
		else if (rowIndex == 0) {
			montage.setFilterEnabled(columnIndex-1, (Boolean) value);

		} else {
			montage.setFilterChannelExcluded(columnIndex-1, rowIndex-1, !((Boolean) value));

		}

	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (rowIndex == 0) {
			if (columnIndex == 0) {
				return false;
			}
			return true;
		}
		if (columnIndex == 0) {
			return true;
		} else {
			return (!montage.isExcludeAllFilters(rowIndex-1) && montage.isFilterEnabled(columnIndex-1));
		}
	}

	public class ColumnTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public int getColumnCount() {
			if (montage == null) {
				return 0;
			}
			return montage.getSampleFilterCount()+1;
		}

		@Override
		public int getRowCount() {
			if (montage == null) {
				return 0;
			}
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return _("All filters");
			} else {
				return columnIndex + ". " + montage.getSampleFilterAt(columnIndex-1).getDescription();
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

	}

	public class RowTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public int getColumnCount() {
			if (montage == null) {
				return 0;
			}
			return 1;
		}

		@Override
		public int getRowCount() {
			if (montage == null) {
				return 0;
			}
			return montage.getMontageChannelCount()+1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex == 0) {
				return _("All channels");
			} else {
				return montage.getMontageChannelLabelAt(rowIndex-1);
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

	}

}
