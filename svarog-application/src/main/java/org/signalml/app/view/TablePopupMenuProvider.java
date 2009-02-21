/* TablePopupMenuProvider.java created 2007-10-19
 * 
 */

package org.signalml.app.view;

import javax.swing.JPopupMenu;

/** TablePopupMenuProvider
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TablePopupMenuProvider extends PopupMenuProvider {

	JPopupMenu getPopupMenu(int col, int row);	
	
}
