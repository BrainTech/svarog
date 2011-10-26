/* SourceMontageTableModel.java created 2007-10-24
 *
 */

package org.signalml.app.montage;

import java.awt.Window;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.domain.montage.IChannelFunction;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.SourceMontageEvent;
import org.signalml.domain.montage.SourceMontageListener;
import org.springframework.context.support.MessageSourceAccessor;
import org.signalml.domain.montage.ChannelType;

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
	private MessageSourceAccessor messageSource;
	private ChannelFunctionsListModel functionsListModel;
	private ChannelsListModel channelsListModel;

	public SourceMontageTableModel() {
		functionsListModel = new ChannelFunctionsListModel();
		channelsListModel = new ChannelsListModel();
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	public ChannelFunctionsListModel getChannelFunctionsListModel() {
		return functionsListModel;
	}

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
				functionsListModel.setConfigurer(montage.getSignalTypeConfigurer());
				channelsListModel.setEegSystem(montage.getEegSystem());
			} else {
				functionsListModel.setConfigurer(null);
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
		if (columnIndex == INDEX_COLUMN) {
			return false;
		}
		return true;
	}

	@Override
	public String getColumnName(int column) {

		switch (column) {

		case INDEX_COLUMN :
			return messageSource.getMessage("sourceMontageTable.index");

		case LABEL_COLUMN :
			return messageSource.getMessage("sourceMontageTable.label");

		case FUNCTION_COLUMN :
			return messageSource.getMessage("sourceMontageTable.function");

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
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
				fireTableCellUpdated(rowIndex, columnIndex);
				return;
			}
			break;

		case FUNCTION_COLUMN :

			try {
				montage.setSourceChannelFunctionAt(rowIndex, (IChannelFunction) value);
			} catch (MontageException ex) {
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
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
