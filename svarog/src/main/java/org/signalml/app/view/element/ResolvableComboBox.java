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

/**
 * Combo-box with the {@link CellRenderer cell renderer} which uses the
 * messages from the source of messages to create the cells.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ResolvableComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;

	/**
	 * the source of messages (labels)
	 */
	private MessageSourceAccessor messageSource;

	/**
	 * Constructor. Sets the source of messages and the {@link CellRenderer
	 * cell renderer}.
	 * @param messageSource the source of messages (labels)
	 */
	public ResolvableComboBox(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;

		setRenderer(new CellRenderer());
	}

	/**
	 * Cell renderer as the text of the cell uses:
	 * <ul>
	 * <li>the text obtained from the source of messages (labels) if the {@code
	 * value} is of type {@code MessageSourceResolvable} or</li>
	 * <li>the default text otherwise.</li></ul>
	 */
	private class CellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		/**
		 * Creates the label which will be used as the contents of the cell.
		 * As the the text of this label uses:
		 * <ul>
		 * <li>the text obtained from the source of messages (labels) if the
		 * {@code value} is of type {@code MessageSourceResolvable} or</li>
		 * <li>the default text otherwise.</li></ul>
		 */
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