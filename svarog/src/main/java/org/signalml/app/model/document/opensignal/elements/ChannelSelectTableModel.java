/* ChannelSelectTableModel.java created 2011-03-23
 *
 */

package org.signalml.app.model.document.opensignal.elements;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * The table model for the ChannelSelectTable.
 *
 * @author Piotr Szachewicz
 */
public class ChannelSelectTableModel extends AbstractTableModel {

	/**
	 * The channels to be shown in this table.
	 */
	private List<AmplifierChannel> channels = new ArrayList<AmplifierChannel>();

	/**
	 * The index of the column which shows whether a channel is selected
	 * or not.
	 */
	public static final int SELECTED_COLUMN = 2;

	/**
	 * The index of the column which shows the channel number.
	 */
	public static final int NUMBER_COLUMN = 0;

	/**
	 * The index of the column which shows the channel label.
	 */
	public static final int LABEL_COLUMN = 1;

	private boolean editable;

	/**
	 * True if the "SELECTED" column should be visible.
	 */
	private boolean showSelectedColumn = false;

	public ChannelSelectTableModel() {
	}

	public ChannelSelectTableModel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		if (openSignalDescriptor == null)
			return;

		if (openSignalDescriptor instanceof ExperimentDescriptor) {
			ExperimentDescriptor experimentDescriptor = (ExperimentDescriptor) openSignalDescriptor;
			setChannels(experimentDescriptor.getAmplifier().getChannels());
			showSelectedColumn = true;

			editable = (experimentDescriptor.getStatus() == ExperimentStatus.NEW);

		}
		else {
			int i = 1;
			for (String channelLabel: openSignalDescriptor.getChannelLabels()) {
				channels.add(new AmplifierChannel(i, channelLabel));
				i++;
			}
			showSelectedColumn = false;
		}
	}

	/**
	 * Sets the channels which represnts the table model.
	 * @param channels the channels which will be contained in this
	 * table model
	 */
	public void setChannels(List<AmplifierChannel> channels) {
		this.channels = channels;
		fireTableDataChanged();
	}

	/**
	 * Returns the AmplifierChannels representing the channels contained in
	 * this model.
	 * @return the channels contained in this model
	 */
	public List<AmplifierChannel> getAmplifierChannels() {
		return channels;
	}

	@Override
	public int getRowCount() {
		return channels.size();
	}

	@Override
	public int getColumnCount() {
		return showSelectedColumn ? 3 : 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		AmplifierChannel channel = channels.get(rowIndex);
		switch (columnIndex) {
		case SELECTED_COLUMN:
			return channel.isSelected();
		case NUMBER_COLUMN:
			return channel.getNumber();
		case LABEL_COLUMN:
			return channel.getLabel();
		default:
			return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 2)
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 1 || columnIndex == 2) {
			return isEditable();
		}
		else
			return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		AmplifierChannel x = channels.get(rowIndex);
		if (columnIndex == 2)
			x.setSelected((Boolean) aValue);
		else if (columnIndex == 1)
			x.setLabel(aValue.toString());

		fireTableCellUpdated(rowIndex, columnIndex);
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case SELECTED_COLUMN:
			return _("selected");
		case NUMBER_COLUMN:
			return _("number");
		case LABEL_COLUMN:
			return _("label");
		default:
			return "";
		}
	}

	/**
	 * Sets all channels in this table model to be selected/unselected.
	 * @param selected the new state of all channels in this table model
	 */
	public void setAllSelected(boolean selected) {
		for (AmplifierChannel channel: channels) {
			channel.setSelected(selected);
		}
		fireTableDataChanged();
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String[] getChannelLabels() {
		String[] channelLabels = new String[channels.size()];
		int i = 0;
		for (AmplifierChannel channel: channels) {
			channelLabels[i++] = channel.getLabel();
		}
		return channelLabels;
	}

}
