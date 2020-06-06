/* MontageWasteBasket.java created 2008-01-04
 *
 */

package org.signalml.app.view.montage.dnd;

import javax.swing.JLabel;
import org.signalml.app.util.IconUtils;

/**
 * The label with the icon of a trash can.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageWasteBasket extends JLabel {

	/**
	 * the default serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates this label with the icon of a trash can.
	 */
	public MontageWasteBasket() {
		setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/trashcan_full.png"));
		setHorizontalAlignment(JLabel.CENTER);
	}

}
