/* TaskStatusCellRenderer.java created 2007-10-19
 * 
 */

package org.signalml.app.view;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.signalml.app.util.IconUtils;
import org.signalml.task.TaskStatus;
import org.springframework.context.support.MessageSourceAccessor;

/** TaskStatusCellRenderer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaskStatusCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		label.setIcon(IconUtils.getTaskIcon( (TaskStatus) value ));
		return label;
	}
	
	@Override
	protected void setValue(Object value) {
		setText( messageSource.getMessage((TaskStatus) value) );
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}
	
}
