/* ActionFocusListener.java created 2007-10-15
 *
 */

package org.signalml.app.action.selector;

import java.util.EventListener;

/** ActionFocusListener
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface ActionFocusListener extends EventListener {

	void actionFocusChanged(ActionFocusEvent e);

}
