/* ChannelSelectTable.java created 2011-03-23
 *
 */

package org.signalml.app.view.document.opensignal.elements;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JTable;

import javax.swing.table.TableColumn;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.OpenMonitorDescriptor;

/**
 * A JTable for selecting channels to be received from an amplifier.
 *
 * @author Piotr Szachewicz
 */
public class ChannelSelectTable extends JTable {

	public ChannelSelectTable() {
		ChannelSelectTableModel tableModel = new ChannelSelectTableModel();

		this.setModel(tableModel);
		setColumnsPreferredSizes();
	}

	/**
	 * Sets the preferred widths for each column.
	 */
	private void setColumnsPreferredSizes() {
		TableColumn column;
		for (int i = 0; i < getColumnCount(); i++) {
			column = getColumnModel().getColumn(i);
			if (i == ChannelSelectTableModel.LABEL_COLUMN) {
				column.setPreferredWidth(100); //column with a name is bigger
			} else {
				column.setPreferredWidth(10);
			}
		}
	}

	/**
	 * Returns the model of this table.
	 * @return the model of this table
	 */
	protected ChannelSelectTableModel getChannelSelectTableModel() {
		return (ChannelSelectTableModel) getModel();
	}

	/**
	 * Returns the channels shown in this table.
	 * @return the channels shown in this tables
	 */
	public List<AmplifierChannel> getAmplifierChannels() {
		return getChannelSelectTableModel().getAmplifierChannels();
	}

	/**
	 * Fills this table with the given descriptor.
	 * @param descriptor the descriptor to be used
	 */
	/*public void fillTableFromModel(AmplifierConnectionDescriptor descriptor) {

		if (descriptor == null || descriptor.getAmplifierInstance() == null) {
                        setModel(new ChannelSelectTableModel());
                        setColumnsPreferredSizes();
                        return;
                }

		AmplifierChannels channels = new AmplifierChannels(
                        descriptor.getAmplifierInstance().getDefinition().getChannelNumbers(),
                        descriptor.getOpenMonitorDescriptor().getChannelLabels());

		ChannelSelectTableModel model = new ChannelSelectTableModel();
		model.setChannels(channels);

		setModel(model);
		setColumnsPreferredSizes();
	}*/

	/**
	 * Fills the given model with the data contained in this table.
	 * @param descriptor the descriptor to be filled
	 */
	/*public void fillModelFromTable(AmplifierConnectionDescriptor descriptor) {
		OpenMonitorDescriptor openMonitorDescriptor = descriptor.getOpenMonitorDescriptor();
		AmplifierChannels amplifierChannels = getAmplifierChannels();

		//setting all channels labels
		String[] labels = amplifierChannels.getAllChannelsLabels();
		openMonitorDescriptor.setChannelLabels(labels);

		//setting selected channels labels
		String[] selectedLabels = amplifierChannels.getSelectedChannelsLabels();
		try {
			openMonitorDescriptor.setSelectedChannelList(selectedLabels);
		} catch (Exception ex) {
		}

	}*/

	/**
	 * Sets all channels in the table to be selected/unselected.
	 * @param selected the new status of each channel in this table
	 */
	public void setAllSelected(boolean selected) {
		getChannelSelectTableModel().setAllSelected(selected);
	}

	public void fillTableFromModel(ExperimentDescriptor descriptor) {

		if (descriptor == null || descriptor.getAmplifier() == null) {
			setModel(new ChannelSelectTableModel());
			setColumnsPreferredSizes();
			return;
		}

		ChannelSelectTableModel model = new ChannelSelectTableModel();
		model.setChannels(descriptor.getAmplifier().getChannels());

		setModel(model);
		setColumnsPreferredSizes();
	}

}
