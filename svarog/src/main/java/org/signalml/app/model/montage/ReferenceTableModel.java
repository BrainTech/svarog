/* ReferenceTableModel.java created 2007-10-24
 *
 */

package org.signalml.app.model.montage;

import java.awt.Window;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.signalml.app.view.components.dialogs.ErrorsDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageEvent;
import org.signalml.domain.montage.MontageListener;
import org.signalml.domain.montage.SourceMontageEvent;
import org.signalml.domain.montage.SourceMontageListener;

/** ReferenceTableModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ReferenceTableModel extends AbstractTableModel implements MontageListener, SourceMontageListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ReferenceTableModel.class);

	private Montage montage;

	private ColumnTableModel columnTableModel;
	private RowTableModel rowTableModel;

	public ReferenceTableModel() {
	}

	public ReferenceTableModel(Montage montage) {
		this.montage = montage;
		if (montage != null) {
			montage.addSourceMontageListener(this);
			montage.addMontageListener(this);
		}
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
				this.montage.removeSourceMontageListener(this);
				this.montage.removeMontageListener(this);
			}
			this.montage = montage;
			if (montage != null) {
				montage.addSourceMontageListener(this);
				montage.addMontageListener(this);
			}
			reset();
		}
	}


	@Override
	public void sourceMontageChannelAdded(SourceMontageEvent ev) {
		reset();
	}

	@Override
	public void sourceMontageChannelChanged(SourceMontageEvent ev) {
		reset();
	}

	@Override
	public void sourceMontageChannelRemoved(SourceMontageEvent ev) {
		reset();
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
		for (int index : ev.getChannels()) {
			fireTableRowsUpdated(index, index);
			if (rowTableModel != null) {
				rowTableModel.fireTableRowsUpdated(index, index);
			}
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
		for (int index : ev.getChannels()) {
			fireTableRowsUpdated(index, index);
			if (rowTableModel != null) {
				rowTableModel.fireTableRowsUpdated(index, index);
			}
		}
	}

	@Override
	public void montageStructureChanged(MontageEvent ev) {
		fireTableDataChanged();
		if (rowTableModel != null) {
			rowTableModel.fireTableDataChanged();
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		if (montage == null) {
			return 0;
		}
		return montage.getSourceChannelCount();
	}

	@Override
	public int getRowCount() {
		if (montage == null) {
			return 0;
		}
		return montage.getMontageChannelCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return montage.getReference(rowIndex, columnIndex);
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {

		try {
			montage.setReference(rowIndex, columnIndex, (String) value);
		} catch (NumberFormatException ex) {
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			fireTableCellUpdated(rowIndex, columnIndex);
			return;
		}

	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex != montage.getMontagePrimaryChannelAt(rowIndex));
	}

	@Override
	public void sourceMontageEegSystemChanged(SourceMontageEvent ev) {
	}

	public class ColumnTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public int getColumnCount() {
			if (montage == null) {
				return 0;
			}
			return montage.getSourceChannelCount();
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
			return montage.getSourceChannelLabelAt(columnIndex);
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
			return montage.getMontageChannelCount();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return montage.getMontageChannelLabelAt(rowIndex);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

	}

}
