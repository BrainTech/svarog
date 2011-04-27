/* ChannelComboBox.java created 2007-10-25
 *
 */

package org.signalml.app.view.element;

import java.awt.Font;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * {@link ResolvableComboBox} with the plain font of size 10 and without border.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelComboBox extends ResolvableComboBox {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * Calls the {@link ResolvableComboBox#ResolvableComboBox(
	 * MessageSourceAccessor) constructor} in {@link ResolvableComboBox parent},
	 * sets the font and that there should be no border.
	 * @param messageSource the source of messages (labels)
	 */
	public ChannelComboBox(MessageSourceAccessor messageSource) {
		super(messageSource);

		setFont(getFont().deriveFont(Font.PLAIN, 10));
		setBorder(null);
	}

}
