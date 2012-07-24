package org.signalml.app.method.ep.view.tags;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.util.ArrayList;
import java.util.List;

import org.signalml.app.model.components.table.AbstractSelectionTableModel;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.TagStyle;

public class TagSelectionTableModel extends AbstractSelectionTableModel {

	public static final int TAG_CHECKBOX_COLUMN_NUMBER = 0;
	public static final int TAG_STYLE_NAME_COLUMN_NUMBER = 1;

	private List<Boolean> selectionStatus = new ArrayList<Boolean>();
	private List<TagStyleGroup> tagStyles = new ArrayList<TagStyleGroup>();

	public void setStyledTagSet(StyledTagSet styledTagSet) {

		tagStyles = new ArrayList<TagStyleGroup>();
		for (TagStyle tagStyle: styledTagSet.getListOfStyles()) {
			TagStyleGroup group = new TagStyleGroup();
			group.addTagStyle(tagStyle);
			tagStyles.add(group);
		}

		this.selectionStatus = new ArrayList<Boolean>();
		for (int i = 0; i < tagStyles.size(); i++)
			selectionStatus.add(false);

		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return tagStyles.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == TAG_CHECKBOX_COLUMN_NUMBER)
			return selectionStatus.get(rowIndex);
		else
			return tagStyles.get(rowIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return getCheckboxColumnNumber() == columnIndex;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == TAG_CHECKBOX_COLUMN_NUMBER) {
			selectionStatus.set(rowIndex, (Boolean) aValue);
		}
	}

	@Override
	protected int getCheckboxColumnNumber() {
		return TAG_CHECKBOX_COLUMN_NUMBER;
	}

	@Override
	public void setAllSelected(boolean selected) {
		for (int i = 0; i < selectionStatus.size(); i++)
			selectionStatus.set(i, selected);
		fireTableDataChanged();
	}

	public void createGroup(int[] rows) {
		TagStyleGroup tagStyleGroup = new TagStyleGroup();
		for (int row: rows) {
			for (TagStyle tagStyle: tagStyles.get(row).getTagStyles()) {
				tagStyleGroup.addTagStyle(tagStyle);
			}
		}

		tagStyles.add(tagStyleGroup);
		selectionStatus.add(false);
		fireTableDataChanged();
	}

	public void deleteGroups(int[] selectedRows) {
		for (int i = selectedRows.length-1; i >= 0; i--) {
			int row = selectedRows[i];
			if (tagStyles.get(row).getNumberOfTagStyles() > 1)
				tagStyles.remove(row);
		}
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case TAG_CHECKBOX_COLUMN_NUMBER: return "";
		case TAG_STYLE_NAME_COLUMN_NUMBER: return _("Tag style");
		}
		return null;
	}

}
