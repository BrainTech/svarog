/* RocTableModel.java created 2007-12-18
 * 
 */

package org.signalml.app.view.roc;

import javax.swing.table.AbstractTableModel;

import org.signalml.domain.roc.RocData;
import org.springframework.context.support.MessageSourceAccessor;

/** RocTableModel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RocTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	
	private RocData rocData;
	
	public RocTableModel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
	}
	
	public RocData getRocData() {
		return rocData;
	}
	
	public void setRocData(RocData rocData) {
		if( this.rocData != rocData ) {
			this.rocData = rocData;
			fireTableStructureChanged();
		}
	}

	@Override
	public int getColumnCount() {
		if( rocData == null ) {
			return 0;
		}
		return rocData.getParameterCount() + 7;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if( columnIndex == 0 ) {
			return Integer.class;
		}
		else if( columnIndex < getColumnCount()-6 ) {
			return Object.class;
		} 
		else {
			return Double.class;
		}
	}
	
	@Override
	public String getColumnName(int column) {
		
		int cnt = getColumnCount();
		if( column == 0 ) {
			return messageSource.getMessage( "roc.tablePointIndex" );
		}
		else if( column == cnt-6 ) {
			return messageSource.getMessage( "roc.tableTP" );
		}
		else if( column == cnt-5 ) {
			return messageSource.getMessage( "roc.tableFP" );
		}
		else if( column == cnt-4 ) {
			return messageSource.getMessage( "roc.tableTN" );
		}
		else if( column == cnt-3 ) {
			return messageSource.getMessage( "roc.tableFN" );
		}
		else if( column == cnt-2 ) {
			return messageSource.getMessage( "roc.tableFPRate" );
		}
		else if( column == cnt-1 ) {
			return messageSource.getMessage( "roc.tableTPRate" );			
		}
		else {
			return messageSource.getMessage( rocData.getParameterAt(column-1) );
		}
		
	}
	
	@Override
	public int getRowCount() {
		if( rocData == null ) {			
			return 0;
		}
		return rocData.getSampleCount();
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		int cnt = getColumnCount();
		
		if( columnIndex == 0 ) {
			return new Integer(rowIndex+1);
		}		
		else if( columnIndex == cnt-6 ) {
			return new Double( rocData.getTruePositiveCount(rowIndex) );
		}
		else if( columnIndex == cnt-5 ) {
			return new Double( rocData.getFalsePositiveCount(rowIndex) );
		}
		else if( columnIndex == cnt-4 ) {
			return new Double( rocData.getTrueNegativeCount(rowIndex) );
		}
		else if( columnIndex == cnt-3 ) {
			return new Double( rocData.getFalseNegativeCount(rowIndex) );
		}		
		else if( columnIndex == cnt-2 ) {
			return new Double( rocData.getFalseRateAt(rowIndex) );
		}
		else if( columnIndex == cnt-1 ) {
			return new Double( rocData.getTrueRateAt(rowIndex) );
		}
		else {			
			return rocData.getParameterValueAt(columnIndex-1, rowIndex);			
		}
		
	}

}
