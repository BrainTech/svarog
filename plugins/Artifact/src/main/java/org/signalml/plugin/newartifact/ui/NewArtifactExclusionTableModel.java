/* ArtifactExclusionTableModel.java created 2007-11-02
 *
 */

package org.signalml.plugin.newartifact.ui;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.plugin.newartifact.data.NewArtifactType;

/** ArtifactExclusionTableModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewArtifactExclusionTableModel extends AbstractTableModel {

	public static final String CHANGED_PROPERTY = "changed";

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(NewArtifactExclusionTableModel.class);

	private int[][] excludedChannels;
	private SourceMontage montage;

	private NewArtifactType[] artifactTypes = NewArtifactType.values();

	private ColumnTableModel columnTableModel;
	private RowTableModel rowTableModel;

	public NewArtifactExclusionTableModel() {
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

	public int[][] getExcludedChannels() {
		return excludedChannels;
	}

	public void setExcludedChannels(int[][] excludedChannels) {
		if (this.excludedChannels != excludedChannels) {
			this.excludedChannels = excludedChannels;
			reset();
		}
	}

	public SourceMontage getMontage() {
		return montage;
	}

	public void setMontage(SourceMontage montage) {
		if (this.montage != montage) {
			this.montage = montage;
			reset();
		}
	}

	public void setExcludedChannelsAndMontage(int[][] excludedChannels, SourceMontage montage) {
		if (this.excludedChannels != excludedChannels || this.montage != montage) {
			this.excludedChannels = excludedChannels;
			this.montage = montage;
			reset();
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Boolean.class;
	}

	@Override
	public int getColumnCount() {
		if (montage == null) {
			return 0;
		}
		return montage.getSourceChannelCount();
	}

	@Override
	public int getRowCount() {
		if (montage == null) {
			return 0;
		}
		return artifactTypes.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return new Boolean(excludedChannels[rowIndex][columnIndex] != 0);
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		excludedChannels[rowIndex][columnIndex] = (((Boolean) value) ? 1 : 0);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	private String getLabel(int channel) {
		return montage.getSourceChannelLabelAt(channel);
	}

	public class ColumnTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public int getColumnCount() {
			if (montage == null) {
				return 0;
			}
			return montage.getSourceChannelCount();
		}

		@Override
		public int getRowCount() {
			if (montage == null) {
				return 0;
			}
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return getLabel(columnIndex);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

	}

	public class RowTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public int getColumnCount() {
			if (montage == null) {
				return 0;
			}
			return 1;
		}

		@Override
		public int getRowCount() {
			if (montage == null) {
				return 0;
			}
			return artifactTypes.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return NewArtifactTypeCaptionHelper.GetCaption(artifactTypes[rowIndex]);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

	}

}