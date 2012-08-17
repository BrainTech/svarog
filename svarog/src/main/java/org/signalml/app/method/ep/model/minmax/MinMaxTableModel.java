package org.signalml.app.method.ep.model.minmax;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.signalml.app.method.ep.helpers.minmax.ChannelStatisticsCalculator;
import org.signalml.method.ep.EvokedPotentialResult;
import org.signalml.util.FormatUtils;

/**
 * A table model for a table containing min/max signal values and times.
 * Its data are taken from the {@link EvokedPotentialResult} set in
 * the {@link MinMaxTableModel#setData(EvokedPotentialResult, int)} method.
 *
 * @author Piotr Szachewicz
 */
public class MinMaxTableModel extends AbstractTableModel {

	private static final int CHANNEL_NAME_COLUMN = 0;
	private static final int MIN_TIME = 1;
	private static final int MIN_VALUE = 2;
	private static final int MAX_TIME = 3;
	private static final int MAX_VALUE = 4;

	private List<ChannelStatistics> statistics;

	public void setData(EvokedPotentialResult result, int tagGroupNumber) {

		ChannelStatisticsCalculator calculator = new ChannelStatisticsCalculator(result, tagGroupNumber);
		statistics = calculator.getStatistics();

		fireTableDataChanged();
	}

	public List<ChannelStatistics> getStatistics() {
		return statistics;
	}

	@Override
	public int getRowCount() {
		if (statistics == null)
			return 0;
		else
			return statistics.size();
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ChannelStatistics channelStatistics = statistics.get(rowIndex);

		switch(columnIndex) {
			case CHANNEL_NAME_COLUMN: return channelStatistics.getChannelName();
			case MIN_TIME: return formatDouble(channelStatistics.getMinTime());
			case MIN_VALUE: return formatDouble(channelStatistics.getMinValue());
			case MAX_TIME: return formatDouble(channelStatistics.getMaxTime());
			case MAX_VALUE: return formatDouble(channelStatistics.getMaxValue());
		}
		return "";
	}

	protected String formatDouble(double value) {
		return FormatUtils.format(value);
	}

	@Override
	public String getColumnName(int column) {
		switch(column) {
			case CHANNEL_NAME_COLUMN: return _("channel");
			case MIN_TIME: return _("min time");
			case MIN_VALUE: return _("min value");
			case MAX_TIME: return _("max time");
			case MAX_VALUE: return _("max value");
		}
		return "";
	}

}
