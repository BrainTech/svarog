/* BookViewFocusSelector.java created 2008-03-05
 * 
 */

package org.signalml.app.action.selector;

import org.signalml.app.view.book.BookView;

/** BookViewFocusSelector
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface BookViewFocusSelector extends ActionFocusSelector {

	BookView getActiveBookView();
	
}
