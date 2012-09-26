package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.i18n.PluginI18n._;
import static org.signalml.plugin.i18n.PluginI18n._R;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.signalml.plugin.newstager.data.NewStagerSleepStatistic;

public class NewStagerTotalStatisticsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1874704341780625482L;

	private static final int NAME_COLUMN = 0;
	private static final int VALUE_COLUMN = 1;

	private abstract class Row {
		private String name;

		public Row(String name) {
			this.name = name;
		}

		String getName() {
			return this.name;
		}

		abstract String getValue(NewStagerSleepStatistic statistic);
	}

	private NewStagerSleepStatistic statistic;
	private Row rows[];

	public NewStagerTotalStatisticsTableModel() {
		this.statistic = null;
		this.rows = null;
		this.init();
	}

	public void init() {
		List<Row> tmpRows = new LinkedList<Row>();

		tmpRows.add(new Row(_("EEG Recording Time")) {

			@Override
			String getValue(NewStagerSleepStatistic statistic) {
				return statistic.getTotalLengthPretty();
			}
		});

		tmpRows.add(new Row(_("Sleep Period Time")) {

			@Override
			String getValue(NewStagerSleepStatistic statistic) {
				return statistic.getSleepPeriodTimePretty();
			}
		});

		tmpRows.add(new Row(_("Total Sleep Time")) {

			@Override
			String getValue(NewStagerSleepStatistic statistic) {
				return statistic.getTotalSleepTimePretty();
			}
		});

		tmpRows.add(new Row(_("Sleep Efficiency Index")) {

			@Override
			String getValue(NewStagerSleepStatistic statistic) {
				return _R("{0}%", statistic.getSleepEfficiencyIndexPretty());
			}
		});

		tmpRows.add(new Row(_("Sleep onset latency")) {

			@Override
			String getValue(NewStagerSleepStatistic statistic) {
				return statistic.getSleepOnsetLatencyPretty();
			}
		});

		tmpRows.add(new Row(_("Sleep onset to SWS")) {

			@Override
			String getValue(NewStagerSleepStatistic statistic) {
				return statistic.getSleepOnsetToSWSPretty();
			}
		});

		tmpRows.add(new Row(_("Sleep onset to REM")) {

			@Override
			String getValue(NewStagerSleepStatistic statistic) {
				return statistic.getSleepOnsetToREMPretty();
			}
		});

		tmpRows.add(new Row(_("Wake periods after sleep onset")) {

			@Override
			String getValue(NewStagerSleepStatistic statistic) {
				return statistic.getWakeInsidePropperSleepTimePretty();
			}
		});

		tmpRows.add(new Row(_("Delta Threshold")) {

			@Override
			String getValue(NewStagerSleepStatistic statistic) {
				return statistic.getDeltaThrPretty();
			}
		});

		tmpRows.add(new Row(_("Alpha Threshold")) {

			@Override
			String getValue(NewStagerSleepStatistic statistic) {
				return statistic.getAlphaThrPretty();
			}
		});

		tmpRows.add(new Row(_("Spindle Threshold")) {

			@Override
			String getValue(NewStagerSleepStatistic statistic) {
				return statistic.getSpindleThrPretty();
			}
		});

		tmpRows.add(new Row(_("EMG Tone")) {

			@Override
			String getValue(NewStagerSleepStatistic statistic) {
				return statistic.getEmgTonePretty();
			}
		});

		this.rows = tmpRows.toArray(new Row[0]);
	}

	public void setStatistic(NewStagerSleepStatistic sleepStatistic) {
		if (this.statistic != sleepStatistic) {
			this.statistic = sleepStatistic;
		}
	}

	@Override
	public int getRowCount() {
		return this.rows != null ? this.rows.length : 0;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {

		case NAME_COLUMN:
			return _("Statistic name");

		case VALUE_COLUMN:
			return _("Value");

		default:
			return "";
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (this.rows == null) {
			return "";
		}

		if (rowIndex < 0 || rowIndex >= this.rows.length) {
			return "";
		}

		Row row = this.rows[rowIndex];
		switch (columnIndex) {
		case NAME_COLUMN:
			return row.getName();
		case VALUE_COLUMN:
			return (this.statistic != null) ? row.getValue(this.statistic) : "";
		default:
			return "";
		}
	}

}
