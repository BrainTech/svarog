package org.signalml.app.method.ep.view.tags;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.util.List;

import javax.swing.JTable;

import org.signalml.app.document.TagDocument;
import org.signalml.app.view.common.components.panels.AbstractSelectionPanel;
import org.signalml.domain.tag.StyledTagSet;

/**
 * This panel can be used to select some tags from the list of tag styles.
 *
 * @author Piotr Szachewicz
 */
public class TagSelectionPanel extends AbstractSelectionPanel<TagSelectionTableModel> {

	public TagSelectionPanel(String label) {
		super(label);
	}

	public TagSelectionPanel() {
		super(_("Tags"));
	}

	@Override
	public JTable getTable() {
		if (table == null)
			table = new TagSelectionTable(getTableModel());
		return table;
	}

	@Override
	public TagSelectionTableModel getTableModel() {
		if (tableModel == null) {
			tableModel = new TagSelectionTableModel();
		}
		return tableModel;
	}

	public void setTagDocument(TagDocument tagDocument) {
		StyledTagSet tagSet = new StyledTagSet();
		if (tagDocument != null) {
			tagSet = tagDocument.getTagSet();
			((TagSelectionTable) getTable()).setStyledTagSet(tagSet);
		}
		getTableModel().setStyledTagSet(tagSet);
	}

	public List<TagStyleGroup> getSelectedTagStyles() {
		return getTableModel().getSelectedElements();
	}

}
