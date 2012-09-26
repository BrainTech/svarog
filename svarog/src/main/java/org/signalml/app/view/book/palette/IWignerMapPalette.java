/* WigneMapPalette.java created 2008-03-03
 *
 */

package org.signalml.app.view.book.palette;

import javax.swing.Icon;

import org.signalml.app.view.I18nMessage;

/** WigneMapPalette
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface IWignerMapPalette extends I18nMessage {

	Icon getIcon();

	int[] getPalette();
}
