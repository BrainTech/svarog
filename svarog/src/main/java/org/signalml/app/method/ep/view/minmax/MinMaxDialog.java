package org.signalml.app.method.ep.view.minmax;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.signalml.app.method.ep.model.minmax.MinMaxTableModel;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.method.ep.EvokedPotentialResult;
import org.signalml.plugin.export.SignalMLException;

/**
 * A dialog containing the min/max averaged evoked potential signal values
 * and the times at which these min/max occured (for each channel).
 * @author Piotr Szachewicz
 */
public class MinMaxDialog extends AbstractDialog {

	private JTabbedPane tabbedPane;
	private List<String> tagStyles;

	public MinMaxDialog() {
		super();
		this.setTitle(_("Min/max dialog"));
		this.setPreferredSize(new Dimension(500, 380));
	}

	@Override
	protected JComponent createInterface() {
		tabbedPane = new JTabbedPane();
		return tabbedPane;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return EvokedPotentialResult.class.isAssignableFrom(clazz);
	}

	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {
		EvokedPotentialResult result = (EvokedPotentialResult) model;

		List<TagStyleGroup> tagStyleGroups = result.getData().getParameters().getAveragedTagStyles();

		tabbedPane.removeAll();
		tagStyles = new ArrayList<String>();
		int i = 0;
		for (TagStyleGroup group: tagStyleGroups) {
			String groupName = group.toString();
			tagStyles.add(groupName);

			MinMaxTableModel tableModel = new MinMaxTableModel();
			tableModel.setData(result, i);
			JTable table = new JTable(tableModel);
			JScrollPane scrollPane = new JScrollPane(table);

			JPanel panel = new JPanel(new BorderLayout());
			panel.add(scrollPane, BorderLayout.CENTER);

			tabbedPane.addTab(groupName, panel);
			i++;
		}

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
	}

}
