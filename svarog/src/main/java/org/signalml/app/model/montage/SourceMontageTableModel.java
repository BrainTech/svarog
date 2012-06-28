/* SourceMontageTableModel.java created 2007-10-24
 *
 */

package org.signalml.app.model.montage;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.domain.montage.system.IChannelFunction;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.SourceMontageEvent;
import org.signalml.domain.montage.SourceMontageListener;

/** SourceMontageTableModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageTableModel extends AbstractTableModel implements SourceMontageListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SourceMontageTableModel.class);

	public static final int INDEX_COLUMN = 0;
	public static final int LABEL_COLUMN = 1;
	public static final int FUNCTION_COLUMN = 2;

	private SourceMontage montage;

	/**
	 * The ListModel managing the list of {@link ChannelFunction ChannelFunctions}
	 * available in the current EEG system.
	 */
	private ChannelFunctionsListModel functionsListModel;
	/**
	 * The ListModel managing the list of channels labels
	 * available in the current EEG system.
	 */
	private ChannelsListModel channelsListModel;

	public SourceMontageTableModel() {
		functionsListModel = new ChannelFunctionsListModel();
		channelsListModel = new ChannelsListModel();
	}

	/**
	 * Returns the ListModel managing the list of {@link ChannelFunction ChannelFunctions}
	 * available in the current EEG system.
	 * @return the ListModel for channel functions
	 */
	public ChannelFunctionsListModel getChannelFunctionsListModel() {
		return functionsListModel;
	}

	/**
	 * Returns the ListModel managing the list of channels labels
	 * available in the current EEG system.
	 * @return the model for channels labels
	 */
	public ChannelsListModel getChannelsListModel() {
		return channelsListModel;
	}

	public SourceMontage getMontage() {
		return montage;
	}

	public void setMontage(SourceMontage montage) {
		if (this.montage != montage) {
			if (this.montage != null) {
				this.montage.removeSourceMontageListener(this);
			}
			this.montage = montage;
			if (montage != null) {
				montage.addSourceMontageListener(this);
				channelsListModel.setEegSystem(montage.getEegSystem());
			} else {
				channelsListModel.setEegSystem(null);
			}

			fireTableDataChanged();
		}
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		if (montage == null)
			return 0;
		return montage.getSourceChannelCount();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		switch (columnIndex) {
		case INDEX_COLUMN:
			return false;
		default:
			if (!montage.getSourceChannelFunctionAt(rowIndex).isMutable()) {
				return false;
			}
			else
				return true;
		}

	}

	@Override
	public String getColumnName(int column) {

		switch (column) {

		case INDEX_COLUMN :
			return _("Index");

		case LABEL_COLUMN :
			return _("Label");

		case FUNCTION_COLUMN :
			return _("Function");

		default :
			throw new IndexOutOfBoundsException();

		}

	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		switch (columnIndex) {

		case INDEX_COLUMN :
				return Integer.class;

		case LABEL_COLUMN :
			return String.class;

		case FUNCTION_COLUMN :
			return IChannelFunction.class;

		default :
			throw new IndexOutOfBoundsException();

		}

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		switch (columnIndex) {

		case INDEX_COLUMN :
			return (rowIndex+1);

		case LABEL_COLUMN :
			return montage.getSourceChannelLabelAt(rowIndex);

		case FUNCTION_COLUMN :
			return montage.getSourceChannelFunctionAt(rowIndex);

		default :
			throw new IndexOutOfBoundsException();

		}

	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {

		if (columnIndex == INDEX_COLUMN || value == null) {
			return;
		}

		switch (columnIndex) {

		case LABEL_COLUMN :

			// this might update function as well
			try {
				montage.setSourceChannelLabelAt(rowIndex, (String) value);
			} catch (MontageException ex) {
				Dialogs.showExceptionDialog((Window) null, ex);
				fireTableCellUpdated(rowIndex, columnIndex);
				return;
			}
			break;

		case FUNCTION_COLUMN :

			try {
				montage.setSourceChannelFunctionAt(rowIndex, (IChannelFunction) value);
			} catch (MontageException ex) {
				Dialogs.showExceptionDialog((Window) null, ex);
				fireTableCellUpdated(rowIndex, columnIndex);
				return;
			}
			break;

		default :
			throw new IndexOutOfBoundsException();

		}

	}

	@Override
	public void sourceMontageChannelAdded(SourceMontageEvent ev) {
		int channel = ev.getChannel();
		fireTableRowsInserted(channel, channel);
	}

	@Override
	public void sourceMontageChannelChanged(SourceMontageEvent ev) {
		int channel = ev.getChannel();
		fireTableRowsUpdated(channel, channel);
	}

	@Override
	public void sourceMontageChannelRemoved(SourceMontageEvent ev) {
		int channel = ev.getChannel();
		fireTableRowsDeleted(channel, channel);
	}

	@Override
	public void sourceMontageEegSystemChanged(SourceMontageEvent ev) {
		channelsListModel.setEegSystem(montage.getEegSystem());
	}

}
