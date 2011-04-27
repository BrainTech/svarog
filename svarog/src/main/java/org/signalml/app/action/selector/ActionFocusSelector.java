/* ActionFocusSelector.java created 2007-11-17
 *
 */

package org.signalml.app.action.selector;

/** ActionFocusSelector
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface ActionFocusSelector {

	void addActionFocusListener(ActionFocusListener listener);
	void removeActionFocusListener(ActionFocusListener listener);

}
