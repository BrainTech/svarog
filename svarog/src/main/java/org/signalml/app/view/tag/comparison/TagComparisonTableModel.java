/* TagComparisonTableModel.java created 2007-12-04
 *
 */

package org.signalml.app.view.tag.comparison;

import java.text.DecimalFormat;
import javax.swing.table.AbstractTableModel;
import org.signalml.domain.tag.TagComparisonResult;
import org.signalml.plugin.export.signal.TagStyle;

/** TagComparisonTableModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagComparisonTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private ColumnTableModel columnTableModel;
	private RowTableModel rowTableModel;

	private DecimalFormat timeFormat = new DecimalFormat("0.00");
	private DecimalFormat percentFormat = new DecimalFormat("0.000");

	private boolean showPercent = false;

	private TagComparisonResult result;

	public TagComparisonTableModel() {
		super();
	}

	public ColumnTableModel getColumnTableModel() {
		if (columnTableModel == null) {
			columnTableModel = new ColumnTableModel();
		}
		return columnTableModel;
	}

	public RowTableModel getRowTableModel() {
		if (rowTableModel == null) {
			rowTableModel = new RowTableModel();
		}
		return rowTableModel;
	}

	private void reset() {
		fireTableStructureChanged();
		if (columnTableModel != null) {
			columnTableModel.fireTableStructureChanged();
		}
		if (rowTableModel != null) {
			rowTableModel.fireTableStructureChanged();
		}
	}

	public boolean isShowPercent() {
		return showPercent;
	}

	public void setShowPercent(boolean showPercent) {
		if (this.showPercent != showPercent) {
			this.showPercent = showPercent;
			fireTableDataChanged();
		}
	}

	public TagComparisonResult getResult() {
		return result;
	}

	public void setResult(TagComparisonResult result) {
		if (this.result != result) {
			this.result = result;
			reset();
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		if (result == null) {
			return 0;
		}
		return result.getBottomStyleCount() + 1;
	}

	@Override
	public int getRowCount() {
		if (result == null) {
			return 0;
		}
		return result.getTopStyleCount() + 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (showPercent) {
			double divider = result.getTopStyleTime(rowIndex-1);
			if (divider == 0) {
				return "-";
			} else {
				return percentFormat.format((result.getStyleOverlay(rowIndex-1, columnIndex-1) * 100) / divider);
			}
		} else {
			return timeFormat.format(result.getStyleOverlay(rowIndex-1, columnIndex-1));
		}
	}


	public class ColumnTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public int getColumnCount() {
			if (result == null) {
				return 0;
			}
			return result.getBottomStyleCount()+1;
		}

		@Override
		public int getRowCount() {
			if (result == null) {
				return 0;
			}
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return null;
			} else {
				return result.getBottomStyleAt(columnIndex-1);
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return TagStyle.class;
		}

	}

	public class RowTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public int getColumnCount() {
			if (result == null) {
				return 0;
			}
			return 1;
		}

		@Override
		public int getRowCount() {
			if (result == null) {
				return 0;
			}
			return result.getTopStyleCount()+1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex == 0) {
				return null;
			} else {
				return result.getTopStyleAt(rowIndex-1);
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return TagStyle.class;
		}

	}

}
