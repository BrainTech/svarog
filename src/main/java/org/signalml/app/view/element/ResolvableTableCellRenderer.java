/* ResolvableTableCellRenderer.java created 2008-03-04
 *
 */

package org.signalml.app.view.element;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

/** ResolvableTableCellRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ResolvableTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	public ResolvableTableCellRenderer(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (value instanceof MessageSourceResolvable) {
			renderer.setText(messageSource.getMessage((MessageSourceResolvable) value));
		}

		return renderer;
	}

}
