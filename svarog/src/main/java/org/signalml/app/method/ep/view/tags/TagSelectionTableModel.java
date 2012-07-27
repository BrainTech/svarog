package org.signalml.app.method.ep.view.tags;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.util.ArrayList;
import java.util.List;

import org.signalml.app.model.components.table.AbstractSelectionTableModel;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.TagStyle;

public class TagSelectionTableModel extends AbstractSelectionTableModel<TagStyleGroup> {

	private StyledTagSet tagSet;

	public void setStyledTagSet(StyledTagSet styledTagSet) {

		if (tagSet == styledTagSet)
			return;
		this.tagSet = styledTagSet;

		elements = new ArrayList<TagStyleGroup>();
		for (TagStyle tagStyle: styledTagSet.getListOfStyles()) {
			TagStyleGroup group = new TagStyleGroup();
			group.addTagStyle(tagStyle);
			elements.add(group);
		}

		this.selectionStatus = new ArrayList<Boolean>();
		for (int i = 0; i < elements.size(); i++)
			selectionStatus.add(false);

		fireTableDataChanged();
	}

	public void setSelectedTagStyles(List<TagStyleGroup> tagStyleGroups) {
		for (TagStyleGroup group: tagStyleGroups) {
			for (int i = 0; i < elements.size(); i++) {
				if (elements.get(i).equals(group)) {
					selectionStatus.set(i, true);
				}
			}
		}
	}

	public void createGroup(int[] rows) {
		TagStyleGroup tagStyleGroup = new TagStyleGroup();
		for (int row: rows) {
			for (TagStyle tagStyle: elements.get(row).getTagStyles()) {
				tagStyleGroup.addTagStyle(tagStyle);
			}
		}

		elements.add(tagStyleGroup);
		selectionStatus.add(false);
		fireTableDataChanged();
	}

	public void deleteGroups(int[] selectedRows) {
		for (int i = selectedRows.length-1; i >= 0; i--) {
			int row = selectedRows[i];
			if (elements.get(row).getNumberOfTagStyles() > 1)
				elements.remove(row);
		}
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case CHECKBOX_COLUMN_NUMBER: return "";
		case ELEMENT_NAME_COLUMN_NUMBER: return _("Tag style");
		}
		return null;
	}

}
