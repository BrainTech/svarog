/* ChannelComboBox.java created 2007-10-25
 *
 */

package org.signalml.app.view.element;

import java.awt.Font;


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
	 * SvarogI18n) constructor} in {@link ResolvableComboBox parent},
	 * sets the font and that there should be no border.
	 */
	public ChannelComboBox() {
		super();

		setFont(getFont().deriveFont(Font.PLAIN, 10));
		setBorder(null);
	}

}
