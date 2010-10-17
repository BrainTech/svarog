/* TagStatisticTableModel.java created 2007-12-04
 *
 */

package org.signalml.app.view.tag.comparison;

import java.text.DecimalFormat;

import javax.swing.table.AbstractTableModel;

import org.signalml.domain.tag.TagStatistic;
import org.signalml.plugin.export.signal.TagStyle;

/** TagStatisticTableModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStatisticTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	public static final int STYLE_COLUMN = 0;
	public static final int TIME_COLUMN = 1;
	public static final int PERCENT_COLUMN = 2;

	private DecimalFormat timeFormat = new DecimalFormat("0.00 s");
	private DecimalFormat percentFormat = new DecimalFormat("0.000 '%'");

	private TagStatistic statistic;
	private float totalLength;

	public TagStatisticTableModel() {
		super();
	}

	public TagStatistic getStatistic() {
		return statistic;
	}

	public void setStatistic(TagStatistic statistic) {
		if (this.statistic != statistic) {
			this.statistic = statistic;
			if (statistic != null) {
				totalLength = statistic.getTotalLength();
			} else {
				totalLength = 0;
			}
			fireTableDataChanged();
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		if (statistic == null) {
			return 0;
		}
		return statistic.getStyleCount()+1;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		switch (columnIndex) {

		case STYLE_COLUMN :
				return TagStyle.class;

		case TIME_COLUMN :
			return String.class;

		case PERCENT_COLUMN :
			return String.class;

		default :
			throw new IndexOutOfBoundsException();

		}

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		switch (columnIndex) {

		case STYLE_COLUMN :
			if (rowIndex == 0) {
				return null;
			} else {
				return statistic.getStyleAt(rowIndex-1);
			}

		case TIME_COLUMN :
			return timeFormat.format(statistic.getStyleTime(rowIndex-1));

		case PERCENT_COLUMN :
			if (totalLength == 0) {
				return "-";
			} else {
				return percentFormat.format((statistic.getStyleTime(rowIndex-1) * 100F) / totalLength);
			}

		default :
			throw new IndexOutOfBoundsException();

		}

	}

}
