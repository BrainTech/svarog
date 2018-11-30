package org.signalml.plugin.loader;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * This class represents a renderer for cells containing
 * check-boxes in the {@link PluginTableModel table} of
 * plug-in {@link PluginState states}.
 * <p>
 * If plug-in has some missing {@link PluginDependency dependencies} or loading
 * of this plug-in failed the cell has red background and the tool-tip is set.
 *
 * @author Marcin Szumski
 */
public class PluginCheckBoxCellRenderer extends JCheckBox implements
	TableCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * an array of plug-in {@link PluginState states}
	 */
	ArrayList<PluginState> descriptions;

	/**
	 * Constructor.
	 * @param descriptions an array of plug-in {@link PluginState states}
	 */
	public PluginCheckBoxCellRenderer(ArrayList<PluginState> descriptions) {
		this.descriptions = descriptions;
		setOpaque(true);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Boolean boolValue = (Boolean) value;
		setSelected(boolValue);
		setHorizontalAlignment(SwingConstants.CENTER);
		PluginState state = descriptions.get(row);
		if (!state.getMissingDependencies().isEmpty() || state.isFailedToLoad()) {
			setBackground(Color.RED);
			String message = new String();
			if (state.isFailedToLoad()) {
				message += _("failed to load");
			} else {
				message += _("missing dependencies") + ": ";
				message += state.missingDependenciesToString();
			}
			setToolTipText(message);
		} else {
			setBackground(Color.WHITE);
			setToolTipText("");
		}
		return this;
	}

}
