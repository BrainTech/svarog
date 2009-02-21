/* SleepComparisonStatisticTableModel.java created 2008-03-03
 * 
 */

package org.signalml.app.method.stager;

import javax.swing.table.AbstractTableModel;

import org.signalml.exception.SanityCheckException;
import org.springframework.context.support.MessageSourceAccessor;

/** SleepComparisonStatisticTableModel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SleepComparisonStatisticTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	public static final int STAGE_COLUMN = 0;
	public static final int CONCORDANCE_COLUMN = 1;
	public static final int SENSITIVITY_COLUMN = 2;
	public static final int SELECTIVITY_COLUMN = 3;
	
	private MessageSourceAccessor messageSource;
	
	private SleepComparison comparison;
			
	public SleepComparisonStatisticTableModel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;		
	}
	
	public SleepComparison getComparison() {
		return comparison;
	}

	public void setComparison(SleepComparison comparison) {
		if( this.comparison != comparison ) {
			this.comparison = comparison;			
			fireTableStructureChanged();
		}
	}
		
	@Override
	public int getColumnCount() {
		return 4;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch( columnIndex ) {
		
		case STAGE_COLUMN :
			return String.class;
			
		case CONCORDANCE_COLUMN :		
			return Double.class;

		case SENSITIVITY_COLUMN :		
			return Double.class;

		case SELECTIVITY_COLUMN :		
			return Double.class;
			
		default :
			throw new SanityCheckException( "Unsupported index [" + columnIndex + "]" );
					
		}
	}

	
	@Override
	public String getColumnName(int column) {
		switch( column ) {
		
		case STAGE_COLUMN :
			return messageSource.getMessage("stagerMethod.dialog.resultReview.comparison.stage");
			
		case CONCORDANCE_COLUMN :
			return messageSource.getMessage("stagerMethod.dialog.resultReview.comparison.concordance");
			
		case SENSITIVITY_COLUMN :
			return messageSource.getMessage("stagerMethod.dialog.resultReview.comparison.sensitivity");

		case SELECTIVITY_COLUMN :
			return messageSource.getMessage("stagerMethod.dialog.resultReview.comparison.selectivity");

		default :
			throw new SanityCheckException( "Unsupported index [" + column + "]" );
					
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	@Override
	public int getRowCount() {
		if( comparison == null ) {
			return 0;
		}
		return comparison.getStyleCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch( columnIndex ) {
		
		case STAGE_COLUMN :
			return comparison.getStyleAt(rowIndex).getDescriptionOrName();
			
		case CONCORDANCE_COLUMN :
			return comparison.getConcordance(rowIndex);
			
		case SENSITIVITY_COLUMN :
			return comparison.getSensitivity(rowIndex);
			
		case SELECTIVITY_COLUMN :
			return comparison.getSelectivity(rowIndex);
						
		default :
			throw new SanityCheckException( "Unsupported index [" + columnIndex + "]" );
					
		}
	}
	
}
