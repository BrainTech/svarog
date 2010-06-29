/* ChannelComboBox.java created 2007-10-25
 *
 */

package org.signalml.app.view.element;

import java.awt.Font;

import org.springframework.context.support.MessageSourceAccessor;

/** ChannelComboBox
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelComboBox extends ResolvableComboBox {

	private static final long serialVersionUID = 1L;

	public ChannelComboBox(MessageSourceAccessor messageSource) {
		super(messageSource);

		setFont(getFont().deriveFont(Font.PLAIN, 10));
		setBorder(null);
	}

}
