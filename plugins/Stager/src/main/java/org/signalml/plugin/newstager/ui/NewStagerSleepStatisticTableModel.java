/* SleepStatisticTableModel.java created 2008-02-23
 *
 */

package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.newstager.NewStagerPlugin._;

import java.util.Arrays;

import javax.swing.table.AbstractTableModel;

import org.signalml.domain.tag.SleepTagName;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.signal.ExportedTagStyle;
import org.signalml.plugin.newstager.data.NewStagerSleepStatistic;

/**
 * SleepStatisticTableModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerSleepStatisticTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	public static final int STAGE_COLUMN = 0;
	public static final int EPOCHS_COLUMN = 1;
	public static final int MINUTES_COLUMN = 2;
	public static final int TST_COLUMN = 3;
	public static final int SPT_COLUMN = 4;

	private NewStagerSleepStatistic statistic;

	private class Row {
		String stage;
		Integer epochs;
		Double minutes;
		Double tst;
		Double spt;
		//Integer order;
	}

	private Row[] data;

	public NewStagerSleepStatisticTableModel() {
		super();
	}

	public NewStagerSleepStatistic getStatistic() {
		return statistic;
	}

	public void setStatistic(NewStagerSleepStatistic statistic) {
		if (this.statistic != statistic) {
			this.statistic = statistic;

			int styleCount = statistic.getStyleCount();
			data = new Row[styleCount + 1];

			int index;
			int addCnt = 0;
			ExportedTagStyle style;

			double segmentLength = statistic.getSegmentLength();
			double tst = statistic.getTotalSleepTime();
			double spt = statistic.getSleepPeriodTime();
			int segmentCount;
			int i;

			for (i = 0; i < styleCount; i++) {

				style = statistic.getStyleAt(i);
				String name = style.getName();
				if (SleepTagName.RK_1.equals(name)
						|| SleepTagName.AASM_N1.equals(name)) {
					index = 0;
				} else if (SleepTagName.RK_2.equals(name)
						   || SleepTagName.AASM_N2.equals(name)) {
					index = 1;
				} else if (SleepTagName.RK_3.equals(name)
						   || SleepTagName.AASM_N3.equals(name)) {
					index = 2;
				} else if (SleepTagName.RK_4.equals(name)) {
					index = 3;
				} else if (SleepTagName.RK_REM.equals(name)
						   || SleepTagName.AASM_REM.equals(name)) {
					index = 5;
				} else if (SleepTagName.RK_WAKE.equals(name)
						   || SleepTagName.AASM_WAKE.equals(name)) {
					index = 6;
				} else if (SleepTagName.RK_MT.equals(name)) {
					index = 7;
				} else {
					index = 8 + addCnt;
					addCnt++;
				}

				data[index] = new Row();

				//data[index].order = index;
				data[index].stage = style.getDescriptionOrName();
				segmentCount = statistic.getStyleSegmentsAt(i);
				data[index].epochs = segmentCount;
				data[index].minutes = (((double)(segmentCount * segmentLength)) / 60.0);
				if (tst > 0) {
					data[index].tst = (((double)(segmentCount * segmentLength) * 100) / tst);
				} else {
					data[index].tst = 0.0;
				}
				if (spt > 0) {
					data[index].spt = (((double)(segmentCount * segmentLength) * 100) / spt);
				} else {
					data[index].spt = 0.0;
				}

			}

			index = 4;

			data[index] = new Row();

			//data[index].order = index;
			data[index].stage = _("SWS");
			int slowSegmentCount = statistic.getSlowSegments();
			data[index].epochs = slowSegmentCount;
			data[index].minutes = (((double)(slowSegmentCount * segmentLength)) / 60.0);
			if (tst > 0) {
				data[index].tst = (((double)(slowSegmentCount * segmentLength) * 100) / tst);
			} else {
				data[index].tst = 0.0;
			}
			if (spt > 0) {
				data[index].spt = (((double)(slowSegmentCount * segmentLength) * 100) / spt);
			} else {
				data[index].spt = 0.0;
			}

			int tgtIndex = 0;
			for (i = 0; i < data.length; i++) {
				if (data[i] == null) {
					continue;
				}
				if (tgtIndex != i) {
					data[tgtIndex] = data[i];
				}
				tgtIndex++;
			}

			data = Arrays.copyOf(data, tgtIndex);

			fireTableStructureChanged();
		}
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {

		case STAGE_COLUMN:
				return String.class;

		case EPOCHS_COLUMN:
			return Integer.class;

		case MINUTES_COLUMN:
		case TST_COLUMN:
		case SPT_COLUMN:
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

		case EPOCHS_COLUMN:
			return _("Epochs");

		case MINUTES_COLUMN:
			return _("Minutes");

		case TST_COLUMN:
			return _("%TST");

		case SPT_COLUMN:
			return _("%SPT");

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
		if (statistic == null) {
			return 0;
		}
		return data.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {

		case STAGE_COLUMN:
			return data[rowIndex].stage;

		case EPOCHS_COLUMN:
			return data[rowIndex].epochs;

		case MINUTES_COLUMN:
			return data[rowIndex].minutes;

		case TST_COLUMN:
			return data[rowIndex].tst;

		case SPT_COLUMN:
			return data[rowIndex].spt;

		default:
			throw new SanityCheckException("Unsupported index [" + columnIndex
										   + "]");

		}
	}

}
