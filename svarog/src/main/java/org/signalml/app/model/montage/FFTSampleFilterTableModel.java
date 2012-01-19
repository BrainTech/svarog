/* FFTSampleFilterTableModel.java created 2008-02-03
 *
 */

package org.signalml.app.model.montage;

import static org.signalml.app.util.i18n.SvarogI18n._;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.FFTSampleFilter.Range;

/** FFTSampleFilterTableModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class FFTSampleFilterTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(FFTSampleFilterTableModel.class);

	public static final int FREQUENCY_COLUMN = 0;
	public static final int COEFFICIENT_COLUMN = 1;

	private FFTSampleFilter filter;

	public FFTSampleFilter getFilter() {
		return filter;
	}

	public void setFilter(FFTSampleFilter filter) {
		if (this.filter != filter) {
			this.filter = filter;
			fireTableDataChanged();
		}
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		if (filter == null) {
			return 0;
		}
		return filter.getRangeCount();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public String getColumnName(int column) {

		switch (column) {

		case FREQUENCY_COLUMN :
			return _("Frequency range [Hz]");

		case COEFFICIENT_COLUMN:
			return _("Coefficient");

		default :
			throw new IndexOutOfBoundsException();

		}

	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		switch (columnIndex) {

		case FREQUENCY_COLUMN :
				return String.class;

		case COEFFICIENT_COLUMN :
			return Double.class;

		default :
			throw new IndexOutOfBoundsException();

		}

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		switch (columnIndex) {

		case FREQUENCY_COLUMN :
			Range range = filter.getRangeAt(rowIndex);
			float lowFrequency = range.getLowFrequency();
			float highFrequency = range.getHighFrequency();
			if (lowFrequency < highFrequency) {
				return lowFrequency + " - " + highFrequency;
			} else {
				return lowFrequency + " - Fn";
			}

		case COEFFICIENT_COLUMN :
			return filter.getRangeAt(rowIndex).getCoefficient();

		default :
			throw new IndexOutOfBoundsException();

		}

	}

	public void onUpdate() {
		fireTableDataChanged();
	}

}
