/* SleepComparisonStatisticTableModel.java created 2008-03-03
 * 
 */

package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.newstager.NewStagerPlugin._;

import javax.swing.table.AbstractTableModel;

import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.newstager.data.NewStagerSleepComparison;

/**
 * SleepComparisonStatisticTableModel
 * 
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerSleepComparisonStatisticTableModel extends
		AbstractTableModel {

	private static final long serialVersionUID = 1L;

	public static final int STAGE_COLUMN = 0;
	public static final int CONCORDANCE_COLUMN = 1;
	public static final int SENSITIVITY_COLUMN = 2;
	public static final int SELECTIVITY_COLUMN = 3;

	private NewStagerSleepComparison comparison;

	public NewStagerSleepComparisonStatisticTableModel() {
		super();
	}

	public NewStagerSleepComparison getComparison() {
		return comparison;
	}

	public void setComparison(NewStagerSleepComparison comparison) {
		if (this.comparison != comparison) {
			this.comparison = comparison;
			fireTableStructureChanged();
		}
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {

		case STAGE_COLUMN:
			return String.class;

		case CONCORDANCE_COLUMN:
			return Double.class;

		case SENSITIVITY_COLUMN:
			return Double.class;

		case SELECTIVITY_COLUMN:
			return Double.class;

		default:
			throw new SanityCheckException("Unsupported index [" + columnIndex
					+ "]");

		}
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {

		case STAGE_COLUMN:
			return _("Stage");

		case CONCORDANCE_COLUMN:
			return _("Concordance %");

		case SENSITIVITY_COLUMN:
			return _("Sensitivity %");

		case SELECTIVITY_COLUMN:
			return _("Selectivity %");

		default:
			throw new SanityCheckException("Unsupported index [" + column + "]");
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public int getRowCount() {
		if (comparison == null) {
			return 0;
		}
		return comparison.getStyleCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {

		case STAGE_COLUMN:
			return comparison.getStyleAt(rowIndex).getDescriptionOrName();

		case CONCORDANCE_COLUMN:
			return comparison.getConcordance(rowIndex);

		case SENSITIVITY_COLUMN:
			return comparison.getSensitivity(rowIndex);

		case SELECTIVITY_COLUMN:
			return comparison.getSelectivity(rowIndex);

		default:
			throw new SanityCheckException("Unsupported index [" + columnIndex
					+ "]");

		}
	}

}
