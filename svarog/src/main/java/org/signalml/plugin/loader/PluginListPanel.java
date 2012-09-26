package org.signalml.plugin.loader;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

/**
 * This panel contains a table with information which plug-ins are active.
 * Allows to select which plug-ins should be active at the next start of
 * the application.
 * <p>
 * For a description of the table see {@link PluginTableModel}.
 *
 * @author Marcin Szumski
 */
public class PluginListPanel extends JPanel {


	private static final long serialVersionUID = 1L;
	/**
	 * an array of plug-in {@link PluginState states}
	 */
	private ArrayList<PluginState> descriptions;
	/**
	 * the {@link PluginTableModel model} for the
	 * {@link #table table} with plug-in {@link PluginState states}
	 */
	private PluginTableModel model;
	/**
	 * a table with information
	 * which plug-ins are active
	 */
	private JTable table;

	/**
	 * Constructor.
	 * Creates the {@link PluginTableModel table} in a scroll pane.
	 * @param pluginDescriptions an array of plug-in
	 * {@link PluginState states}
	 */
	public PluginListPanel(ArrayList<PluginState> pluginDescriptions) {
		CompoundBorder cb = new CompoundBorder(
			new TitledBorder("List of plugins"),
			null
		);

		setBorder(cb);

		descriptions = pluginDescriptions;
		model = new PluginTableModel(descriptions);
		table = new JTable(model);
		table.setDefaultRenderer(String.class, new PluginLabelCellRenderer(descriptions));
		table.setDefaultRenderer(Boolean.class, new PluginCheckBoxCellRenderer(descriptions));
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		add(scrollPane);
	}

	/**
	 * Fills the table (actually the model) with the given data.
	 * @param pluginDescriptions an array of plug-in {@link PluginState states}
	 */
	public void fillPanelFromModel(ArrayList<PluginState> pluginDescriptions) {
		model.fromModel(pluginDescriptions);
	}

	/**
	 * Using the given {@link PluginTableModel model} updates the
	 * data which plug-ins are active.
	 * @param descriptions an array of plug-in {@link PluginState states}
	 */
	public void fillModelFromPanel(ArrayList<PluginState> descriptions) {
		model.fillModel(descriptions);
	}


}
