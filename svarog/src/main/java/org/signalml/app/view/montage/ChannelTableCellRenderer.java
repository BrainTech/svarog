/* CenteringCellRenderer.java created 2007-10-24
 *
 */

package org.signalml.app.view.montage;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.signalml.domain.montage.system.IChannelFunction;
import org.springframework.context.support.MessageSourceAccessor;

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
	 * the source of messages (labels)
	 */
	private MessageSourceAccessor messageSource;

	/**
	 * Returns the label obtained from
	 * {@link DefaultTableCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
	 * super method} with the function of the channel.
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		label.setText(messageSource.getMessage((IChannelFunction) value));
		return label;
	}

	/**
	 * Returns the source of messages (labels).
	 * @return the source of messages (labels)
	 */
	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	/**
	 * Sets the source of messages (labels).
	 * @param messageSource the source of messages (labels)
	 */
	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

}
