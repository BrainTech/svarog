/* ResolvableComboBox.java created 2007-10-26
 *
 */

package org.signalml.app.view.element;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

/** ResolvableComboBox
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ResolvableComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	public ResolvableComboBox(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;

		setRenderer(new CellRenderer());
	}

	private class CellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof MessageSourceResolvable) {
				label.setText(messageSource.getMessage((MessageSourceResolvable) value));
			}
			// else leave text put by superclass
			return label;
		}

	}

}
