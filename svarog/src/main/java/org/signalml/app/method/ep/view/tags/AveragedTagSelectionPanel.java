package org.signalml.app.method.ep.view.tags;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.signalml.app.model.components.table.AbstractSelectionTableModel;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.method.ep.EvokedPotentialParameters;

public class AveragedTagSelectionPanel extends TagSelectionPanel implements ListSelectionListener {

	private JButton groupTagsButton;
	private JButton ungroupTagsButton;

	public AveragedTagSelectionPanel() {
		super(_("Averaged tags"));

		getTable().getSelectionModel().addListSelectionListener(this);
	}

	public JButton getGroupTagsButton() {
		if (groupTagsButton == null) {
			groupTagsButton = new JButton(new AbstractAction(_("Group")) {

				@Override
				public void actionPerformed(ActionEvent e) {
					TagSelectionTableModel model = (TagSelectionTableModel) getTableModel();
					model.createGroup(table.getSelectedRows());
				}
			});
			groupTagsButton.setEnabled(false);
		}
		return groupTagsButton;
	}

	public JButton getUngroupTagsButton() {
		if (ungroupTagsButton == null) {
			ungroupTagsButton = new JButton(new AbstractAction(_("Ungroup")) {

				@Override
				public void actionPerformed(ActionEvent e) {
					TagSelectionTableModel model = (TagSelectionTableModel) getTableModel();
					model.deleteGroups(table.getSelectedRows());
				}
			});
			ungroupTagsButton.setEnabled(false);
		}
		return ungroupTagsButton;
	}

	@Override
	protected JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel(new BorderLayout());
		buttonsPanel.add(super.createButtonsPanel(), BorderLayout.WEST);
		buttonsPanel.add(createRightButtonsPanel(), BorderLayout.EAST);
		return buttonsPanel;
	}

	protected JPanel createRightButtonsPanel() {
		JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightButtons.add(getGroupTagsButton());
		rightButtons.add(getUngroupTagsButton());
		return rightButtons;
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		int[] selectedRows = table.getSelectedRows();
		getGroupTagsButton().setEnabled(selectedRows.length > 1);

		int realGroupsCount = 0;
		for (int row: selectedRows) {
			TagSelectionTableModel model = getTableModel();

			TagStyleGroup group = (TagStyleGroup) model.getValueAt(row, AbstractSelectionTableModel.ELEMENT_NAME_COLUMN_NUMBER);

			if (group.getTagStyleNames().size() > 1)
				realGroupsCount++;
		}
		getUngroupTagsButton().setEnabled(realGroupsCount > 0);
	}



	public void fillModelFromPanel(EvokedPotentialParameters parameters) {
		parameters.setAveragedTagStyles(getSelectedTagStyles());
	}

	@Override
	public void validatePanel(ValidationErrors errors) {
		List<TagStyleGroup> selectedElements = getTableModel().getSelectedElements();

		if (selectedElements.size() == 0) {
			errors.addError(_("Please select at least one tag to average."));
		}

	}

	public void fillPanelFromModel(EvokedPotentialParameters parameters) {
		getTableModel().setSelectedTagStyles(parameters.getAveragedTagStyles());
	}

}
