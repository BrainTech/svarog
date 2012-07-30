/* SleepStatisticTableModel.java created 2008-02-23
 *
 */

package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.i18n.PluginI18n._;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

import org.signalml.domain.tag.SleepTagName;
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

	private static final String SWS = "SWS";
	
	private NewStagerSleepStatistic statistic;

	private class Row {
		public final String name;
		public final String stage;
		public final Integer epochs;
		public final Double minutes;
		public final Double tst;
		public final Double spt;

		public Row(String name, String stage, int segmentCount, double segmentLength,
				double tst, double spt) {
			this.name = name;
			this.stage = stage;
			this.epochs = segmentCount;
			this.minutes = (((double) (segmentCount * segmentLength)) / 60.0);
			this.tst = tst > 0 ? (((double) (segmentCount * segmentLength) * 100) / tst)
					: 0.0;
			this.spt = spt > 0 ? (((double) (segmentCount * segmentLength) * 100) / spt)
					: 0.0;
		}
	}

	private Row data[];

	public NewStagerSleepStatisticTableModel() {
		super();
	}

	public NewStagerSleepStatistic getStatistic() {
		return statistic;
	}

	public void setStatistic(NewStagerSleepStatistic statistic) {
		if (this.statistic != statistic) {
			this.statistic = statistic;

			LinkedList<Row> list = new LinkedList<Row>();

			ExportedTagStyle style;

			double segmentLength = statistic.getSegmentLength();
			double tst = statistic.getTotalSleepTime();
			double spt = statistic.getSleepPeriodTime();

			for (int i = 0; i < statistic.getStyleCount(); i++) {
				style = statistic.getStyleAt(i);
				
				list.add(new Row(style.getName(), style.getDescriptionOrName(),
							statistic.getStyleSegmentsAt(i),
							segmentLength, tst, spt));
			}

			

			list.add(new Row(SWS, _("SWS"), statistic.getSlowSegments(), segmentLength, tst, spt));

			data = list.toArray(new Row[0]);
			Arrays.sort(data, new Comparator<Row>() {

				private int weight(Row r) {
					String name = r.name;
					if (SleepTagName.RK_1.equals(name) || SleepTagName.AASM_N1.equals(name)) {
						return 0;
					} else if (SleepTagName.RK_2.equals(name)
							|| SleepTagName.AASM_N2.equals(name)) {
						return 1;
					} else if (SleepTagName.RK_3.equals(name)
							|| SleepTagName.AASM_N3.equals(name)) {
						return 2;
					} else if (SleepTagName.RK_4.equals(name)) {
						return 3;
					} else if (name.equals(SWS)) {
						return 4;
					} else if (SleepTagName.RK_REM.equals(name)
							|| SleepTagName.AASM_REM.equals(name)) {
						return 5;
					} else if (SleepTagName.RK_WAKE.equals(name)
							|| SleepTagName.AASM_WAKE.equals(name)) {
						return 6;
					} else if (SleepTagName.RK_MT.equals(name)) {
						return 7;
					} else {
						return 8;
					}

				}

				
				@Override
				public int compare(Row r1, Row r2) {
					return new Integer(this.weight(r1)).compareTo(this.weight(r2));
				}
			});
			
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
			return null;
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
			return "";
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
			return "";
		}
	}

}
