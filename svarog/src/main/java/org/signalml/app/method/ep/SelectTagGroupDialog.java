package org.signalml.app.method.ep;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.plugin.export.SignalMLException;

/**
 * A dialog for selecting which tag group should be exported.
 *
 * @author Piotr Szachewicz
 */
public class SelectTagGroupDialog extends AbstractDialog {

	private JComboBox selectTagGroupComboBox;

	public SelectTagGroupDialog() {
		super();
		setModal(true);
		setLocationRelativeTo(null);
	}

	@Override
	protected JComponent createInterface() {
		AbstractPanel panel = new AbstractPanel(_("Select tag group"));
		panel.setLayout(new BorderLayout());
		panel.add(getSelectTagGroupComboBox(), BorderLayout.CENTER);
		return panel;
	}

	public JComboBox getSelectTagGroupComboBox() {
		if (selectTagGroupComboBox == null) {
			selectTagGroupComboBox = new JComboBox();
		}
		return selectTagGroupComboBox;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return true;
	}

	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {
		List<TagStyleGroup> groups = (List<TagStyleGroup>) model;
		getSelectTagGroupComboBox().setModel(new DefaultComboBoxModel(groups.toArray(new TagStyleGroup[0])));
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		List<TagStyleGroup> groups = (List<TagStyleGroup>) model;
		groups.clear();
		groups.add((TagStyleGroup) selectTagGroupComboBox.getSelectedItem());
	}

}