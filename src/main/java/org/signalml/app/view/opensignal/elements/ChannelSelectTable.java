/* ChannelSelectTable.java created 2011-03-23
 *
 */

package org.signalml.app.view.opensignal.elements;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.signalml.app.model.AmplifierConnectionDescriptor;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.worker.amplifiers.AmplifierDefinition;

/**
 *
 * @author Piotr Szachewicz
 */
public class ChannelSelectTable extends JTable {

	public ChannelSelectTable() {
		ChannelSelectTableModel tableModel = new ChannelSelectTableModel();
		this.setModel(tableModel);
		setColumnsPreferredSizes();
	}

	private void setColumnsPreferredSizes() {
		TableColumn column;
		for (int i = 0; i < getColumnCount(); i++) {
			column = getColumnModel().getColumn(i);
			if (i == ChannelSelectTableModel.NAME_COLUMN) {
				column.setPreferredWidth(100); //column with a name is bigger
			} else {
				column.setPreferredWidth(10);
			}
		}
	}

	protected ChannelSelectTableModel getChannelSelectTableModel() {
		return (ChannelSelectTableModel) getModel();
	}

	public AmplifierChannels getAmplifierChannels() {
		return getChannelSelectTableModel().getAmplifierChannels();
	}

	public void fillTableFromModel(AmplifierConnectionDescriptor descriptor) {

		if (descriptor == null || descriptor.getAmplifierInstance() == null) {
                        AmplifierChannels channels = new AmplifierChannels(new ArrayList<Integer>());
                        ChannelSelectTableModel model = new ChannelSelectTableModel();
                        model.setChannels(channels);
                        setModel(model);
                        setColumnsPreferredSizes();
                        return;
                }

		AmplifierDefinition amplifierDefinition = descriptor.getAmplifierInstance().getDefinition();

		List<Integer> channelNumbers = amplifierDefinition.getChannelNumbers();

		AmplifierChannels channels = new AmplifierChannels(channelNumbers);

		ChannelSelectTableModel model = new ChannelSelectTableModel();
		model.setChannels(channels);

		setModel(model);
		setColumnsPreferredSizes();
	}

	public void setAllSelected(boolean selected) {
		getChannelSelectTableModel().setAllSelected(selected);
	}

	public void fillModelFromTable(AmplifierConnectionDescriptor descriptor) {
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

	}

}
