/* TreePopupMenuProvider.java created 2007-10-15
 * 
 */

package org.signalml.app.view;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

/** TreePopupMenuProvider
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TreePopupMenuProvider extends PopupMenuProvider {

	JPopupMenu getPopupMenu(TreePath path);
	
}
