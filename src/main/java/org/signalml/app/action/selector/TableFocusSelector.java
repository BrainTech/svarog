/* TableFocusSelector.java created 2007-12-07
 * 
 */

package org.signalml.app.action.selector;

import javax.swing.JTable;

/** TableFocusSelector
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TableFocusSelector extends ActionFocusSelector {

	JTable getActiveTable();
	
}
