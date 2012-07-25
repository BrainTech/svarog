/* MontageTableModel.java created 2007-11-23
 *
 */

package org.signalml.app.model.montage;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageEvent;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.MontageListener;
import org.signalml.domain.montage.SourceMontageEvent;
import org.signalml.domain.montage.SourceMontageListener;

/** MontageTableModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageTableModel extends AbstractTableModel implements SourceMontageListener, MontageListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MontageTableModel.class);

	public static final int INDEX_COLUMN = 0;
	public static final int PRIMARY_LABEL_COLUMN = 1;
	public static final int LABEL_COLUMN = 2;

	private Montage montage;

	public MontageTableModel() {
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
			fireTableDataChanged();
		}
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		if (montage == null) {
			return 0;
		}
		return montage.getMontageChannelCount();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == INDEX_COLUMN || columnIndex == PRIMARY_LABEL_COLUMN) {
			return false;
		}
		return true;
	}

	@Override
	public String getColumnName(int column) {

		switch (column) {

		case INDEX_COLUMN :
			return _("Index");

		case PRIMARY_LABEL_COLUMN :
			return _("Primary label");

		case LABEL_COLUMN :
			return _("Label");

		default :
			throw new IndexOutOfBoundsException();

		}

	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		switch (columnIndex) {

		case INDEX_COLUMN :
				return Integer.class;

		case PRIMARY_LABEL_COLUMN :
			return String.class;

		case LABEL_COLUMN :
			return String.class;

		default :
			throw new IndexOutOfBoundsException();

		}

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		switch (columnIndex) {

		case INDEX_COLUMN :
			return (rowIndex+1);

		case PRIMARY_LABEL_COLUMN :
			return montage.getReferenceReadable(rowIndex);

		case LABEL_COLUMN :
			return montage.getMontageChannelLabelAt(rowIndex);

		default :
			throw new IndexOutOfBoundsException();

		}

	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {

		if (columnIndex == INDEX_COLUMN) {
			return;
		}

		if (columnIndex == LABEL_COLUMN) {
			try {
				montage.setMontageChannelLabelAt(rowIndex, (String) value);
			} catch (MontageException ex) {
				Dialogs.showExceptionDialog((Window) null, ex);
				fireTableDataChanged();
				return;
			}
		} else {
			throw new IndexOutOfBoundsException();
		}

	}

	@Override
	public void sourceMontageChannelAdded(SourceMontageEvent ev) {
		// this doesn't immediately affect this table
	}

	@Override
	public void sourceMontageChannelChanged(SourceMontageEvent ev) {
		// this may affect any row
		fireTableRowsUpdated(0, getRowCount()-1);
	}

	@Override
	public void sourceMontageChannelRemoved(SourceMontageEvent ev) {
		// this may affect any row and remove any number of rows
		fireTableDataChanged();
	}

	@Override
	public void montageChannelsAdded(MontageEvent ev) {
		int[] indices = ev.getChannels();
		if (indices.length == 0) {
			return;
		}
		for (int i=0; i<(indices.length-1); i++) {
			if (indices[i] != (indices[i+1]-1)) {
				// if non-contiguous fire a total update
				fireTableDataChanged();
				return;
			}
		}
		// if contiguous fire an insert
		fireTableRowsInserted(indices[0], indices[indices.length-1]);
	}

	@Override
	public void montageChannelsChanged(MontageEvent ev) {
		int[] indices = ev.getChannels();
		for (int i=0; i<indices.length; i++) {
			fireTableRowsUpdated(indices[i], indices[i]);
		}
	}

	@Override
	public void montageChannelsRemoved(MontageEvent ev) {
		int[] indices = ev.getChannels();
		if (indices.length == 0) {
			return;
		}
		for (int i=0; i<(indices.length-1); i++) {
			if (indices[i] != (indices[i+1]-1)) {
				// if non-contiguous fire a total update
				fireTableDataChanged();
				return;
			}
		}
		// if contiguous fire a delete
		fireTableRowsDeleted(indices[0], indices[indices.length-1]);
	}

	@Override
	public void montageReferenceChanged(MontageEvent ev) {
		// this is not reflected in this table so far
	}

	@Override
	public void montageStructureChanged(MontageEvent ev) {
		fireTableDataChanged();
	}

	@Override
	public void sourceMontageEegSystemChanged(SourceMontageEvent ev) {
	}

}
