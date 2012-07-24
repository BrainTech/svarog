package org.signalml.app.method.ep.view.tags;

import static org.signalml.app.util.i18n.SvarogI18n._;

import javax.swing.JTable;

import org.signalml.app.document.TagDocument;
import org.signalml.app.method.ep.EvokedPotentialApplicationData;
import org.signalml.app.model.components.table.AbstractSelectionTableModel;
import org.signalml.app.view.common.components.panels.AbstractSelectionPanel;

public class TagSelectionPanel extends AbstractSelectionPanel {

	private TagSelectionTableModel tableModel;

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
	public AbstractSelectionTableModel getTableModel() {
		if (tableModel == null) {
			tableModel = new TagSelectionTableModel();
		}
		return tableModel;
	}

	public void fillPanelFromModel(EvokedPotentialApplicationData data) {
		TagDocument tagDocument = data.getTagDocument();

		if (tagDocument != null)
			((TagSelectionTableModel)getTableModel()).setStyledTagSet(tagDocument.getTagSet());
	}

	public void fillModelFromPanel(EvokedPotentialApplicationData data) {

	}

}
