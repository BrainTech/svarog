/* ActionFocusEvent.java created 2007-10-15
 * 
 */

package org.signalml.app.action.selector;

import java.util.EventObject;

/** ActionFocusEvent
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ActionFocusEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	public ActionFocusEvent(Object source) {
		super(source);
	}

	public ActionFocusManager getActionFocusManager() {
		if( !(source instanceof ActionFocusManager) ) {
			return null;
		}
		return (ActionFocusManager) source;
	}
	
}
