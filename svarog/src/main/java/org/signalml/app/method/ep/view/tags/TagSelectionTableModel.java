package org.signalml.app.method.ep.view.tags;

import java.util.ArrayList;
import java.util.List;
import org.signalml.app.model.components.table.AbstractSelectionTableModel;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * This is a table model for {@link TagSelectionTable}.
 *
 * @author Piotr Szachewicz
 */
public class TagSelectionTableModel extends AbstractSelectionTableModel<TagStyleGroup> {

	public void setStyledTagSet(StyledTagSet styledTagSet) {

		elements = new ArrayList<TagStyleGroup>();
		for (TagStyle tagStyle: styledTagSet.getListOfStyles()) {
			TagStyleGroup group = new TagStyleGroup();
			group.addTagStyle(tagStyle.getName());
			elements.add(group);
		}

		this.selectionStatus = new ArrayList<Boolean>();
		for (TagStyleGroup element : elements) {
			selectionStatus.add(false);
		}

		fireTableDataChanged();
	}

	public void setSelectedTagStyles(List<TagStyleGroup> tagStyleGroups) {
		setAllSelected(false);

		for (TagStyleGroup group: tagStyleGroups) {
			Integer index = findGroup(group);
			if (index != null) {
				selectionStatus.set(index, true);
			} else {
				Integer newGroupIndex = createGroupIfTagsAreAvailable(group);
				if (newGroupIndex != null)
					selectionStatus.set(newGroupIndex, true);
			}
		}
		fireTableDataChanged();
	}

	protected Integer findGroup(TagStyleGroup group) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).equals(group)) {
				return i;
			}
		}
		return null;
	}

	protected Integer createGroupIfTagsAreAvailable(TagStyleGroup group) {
		if (group.getNumberOfTagStyles() == 1)
			return null;

		List<String> tagStyleNames = group.getTagStyleNames();
		int[] indices = new int[tagStyleNames.size()];
		for (int i = 0; i < tagStyleNames.size(); i++) {
			String tagStyleName = tagStyleNames.get(i);
			Integer index = findGroup(new TagStyleGroup(tagStyleName));
			if (index == null)
				return null;
			indices[i] = index;
		}

		return createGroup(indices);
	}

	public Integer createGroup(int[] rows) {
		TagStyleGroup tagStyleGroup = new TagStyleGroup();
		for (int row: rows) {
			for (String tagStyle: elements.get(row).getTagStyleNames()) {
				tagStyleGroup.addTagStyle(tagStyle);
			}
		}

		elements.add(tagStyleGroup);
		selectionStatus.add(false);
		fireTableDataChanged();
		return elements.size()-1;
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
