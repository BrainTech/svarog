/* ChannelSelectTable.java created 2011-03-23
 *
 */

package org.signalml.app.view.document.opensignal.elements;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.AmplifierChannel;
import org.signalml.app.model.document.opensignal.elements.ChannelSelectTableModel;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;
import org.signalml.app.view.components.GrayTableCellRenderer;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;

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
	 * Sets all channels in the table to be selected/unselected.
	 * @param selected the new status of each channel in this table
	 */
	public void setAllSelected(boolean selected) {
		getChannelSelectTableModel().setAllSelected(selected);
	}
	
	public void fillTableFromModel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		ChannelSelectTableModel model = new ChannelSelectTableModel(openSignalDescriptor);

		setModel(model);
		setColumnsPreferredSizes();
	}

	public String[] getChannelLabels() {
		ChannelSelectTableModel model = (ChannelSelectTableModel) this.getModel();
		return model.getChannelLabels();
	}

}
