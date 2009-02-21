/* WigneMapPalette.java created 2008-03-03
 * 
 */

package org.signalml.app.view.book;

import javax.swing.Icon;

import org.springframework.context.MessageSourceResolvable;

/** WigneMapPalette
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface WignerMapPalette extends MessageSourceResolvable {

	Icon getIcon();
	
	int[] getPalette();
	
}
