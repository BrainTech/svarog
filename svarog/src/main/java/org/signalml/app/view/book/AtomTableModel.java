/* AtomTableModel.java created 2008-02-28
 *
 */

package org.signalml.app.view.book;

import static org.signalml.app.SvarogApplication._;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.signalml.domain.book.SegmentReconstructionProvider;
import org.signalml.domain.book.StandardBookSegment;
import org.signalml.exception.SanityCheckException;

/** AtomTableModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class AtomTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final int ITERATION_COLUMN = 0;
	private static final int MODULUS_COLUMN = 1;
	private static final int AMPLITUDE_COLUMN = 2;
	private static final int POSITION_COLUMN = 3;
	private static final int SCALE_COLUMN = 4;
	private static final int FREQUENCY_COLUMN = 5;
	private static final int PHASE_COLUMN = 6;
	private static final int RECONSTRUCTION_COLUMN = 7;
	private StandardBookSegment segment;
	private SegmentReconstructionProvider reconstruction;

	private TableRowSorter<AtomTableModel> sorter = null;

	public  AtomTableModel() {
	}

	public TableRowSorter<AtomTableModel> getSorter() {
		if (sorter == null) {
			sorter = new TableRowSorter<AtomTableModel>(this);
			sorter.setSortsOnUpdates(true);
		}
		return sorter;
	}

	public StandardBookSegment getSegment() {
		return segment;
	}

	public void setSegment(StandardBookSegment segment) {
		if (this.segment != segment) {
			this.segment = segment;
			fireTableDataChanged();
		}
	}

	public SegmentReconstructionProvider getReconstruction() {
		return reconstruction;
	}

	public void setReconstruction(SegmentReconstructionProvider reconstruction) {
		if (this.reconstruction != reconstruction) {
			if (reconstruction != null) {
				if (segment == null || segment != reconstruction.getSegment()) {
					throw new SanityCheckException("Reconstruction doesn't match the segment");
				}
			}
			SegmentReconstructionProvider oldReconstruction = this.reconstruction;
			this.reconstruction = reconstruction;
			if (oldReconstruction == null || reconstruction == null) {
				fireTableStructureChanged();
			} else {
				fireTableDataChanged();
			}
		}
	}

	@Override
	public int getColumnCount() {
		return 7 + (reconstruction != null ? 1 : 0);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		switch (columnIndex) {

		case ITERATION_COLUMN :
				return Integer.class;

		case MODULUS_COLUMN :
			return Double.class;

		case AMPLITUDE_COLUMN :
			return Double.class;

		case POSITION_COLUMN :
			return Double.class;

		case SCALE_COLUMN :
			return Double.class;

		case FREQUENCY_COLUMN :
			return Double.class;

		case PHASE_COLUMN :
			return Double.class;

		case RECONSTRUCTION_COLUMN :
			return Boolean.class;

		default :
			throw new IllegalArgumentException("No such column [" + columnIndex + "]");

		}

	}

	@Override
	public String getColumnName(int column) {

		switch (column) {

		case ITERATION_COLUMN :
			return _("Iteration");

		case MODULUS_COLUMN :
			return _("Modulus");

		case AMPLITUDE_COLUMN :
			return _("Amplitude");

		case POSITION_COLUMN :
			return _("Position");

		case SCALE_COLUMN :
			return _("Scale");

		case FREQUENCY_COLUMN :
			return _("Frequency");

		case PHASE_COLUMN :
			return _("Phase");

		case RECONSTRUCTION_COLUMN :
			return _("Reconstruct");

		default :
			throw new IllegalArgumentException("No such column [" + column + "]");

		}

	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == RECONSTRUCTION_COLUMN) {
			return true;
		}
		return false;
	}

	@Override
	public int getRowCount() {
		if (segment == null) {
			return 0;
		}
		return segment.getAtomCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		switch (columnIndex) {

		case ITERATION_COLUMN :
			return segment.getAtomAt(rowIndex).getIteration();

		case MODULUS_COLUMN :
			return new Double(segment.getAtomAt(rowIndex).getModulus());

		case AMPLITUDE_COLUMN :
			return new Double(segment.getAtomAt(rowIndex).getAmplitude());

		case POSITION_COLUMN :
			return new Double(segment.getAtomAt(rowIndex).getTimePosition());

		case SCALE_COLUMN :
			return new Double(segment.getAtomAt(rowIndex).getTimeScale());

		case FREQUENCY_COLUMN :
			return new Double(segment.getAtomAt(rowIndex).getHzFrequency());

		case PHASE_COLUMN :
			return new Double(segment.getAtomAt(rowIndex).getPhase());

		case RECONSTRUCTION_COLUMN :
			if (reconstruction == null) {
				return new Boolean(false);
			} else {
				return reconstruction.isAtomInSelectiveReconstruction(rowIndex);
			}

		default :
			throw new IllegalArgumentException("No such column [" + columnIndex + "]");

		}

	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {

		if (reconstruction == null) {
			return;
		}

		if (columnIndex == RECONSTRUCTION_COLUMN) {
			if (((Boolean) value).booleanValue()) {
				reconstruction.addAtomToSelectiveReconstruction(rowIndex);
			} else {
				reconstruction.removeAtomFromSelectiveReconstruction(rowIndex);
			}

			fireTableCellUpdated(rowIndex, columnIndex);
		}

	}

}
