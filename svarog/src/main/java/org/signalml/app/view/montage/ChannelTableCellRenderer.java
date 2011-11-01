/* CenteringCellRenderer.java created 2007-10-24
 *
 */

package org.signalml.app.view.montage;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.signalml.domain.montage.Channel;

/**
 * Cell renderer for tables with signal channel, which displays the
 * {@link Channel function} of the channel. 
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelTableCellRenderer extends DefaultTableCellRenderer {

	/**
	 * the default serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Returns the label obtained from
	 * {@link DefaultTableCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
	 * super method} with the function of the channel.
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		label.setText(getSvarogI18n().getMessage((Channel) value));
		return label;
	}

	/**
	 * Returns the {@link SvarogAccessI18nImpl} instance.
	 * @return the {@link SvarogAccessI18nImpl} singleton instance
	 */
	protected org.signalml.app.SvarogI18n getSvarogI18n() {
		return org.signalml.plugin.impl.SvarogAccessI18nImpl.getInstance();
	}
}
