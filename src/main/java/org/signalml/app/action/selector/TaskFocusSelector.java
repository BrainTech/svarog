/* TaskFocusSelector.java created 2007-11-17
 *
 */

package org.signalml.app.action.selector;

import org.signalml.task.Task;

/** TaskFocusSelector
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TaskFocusSelector extends ActionFocusSelector {

	Task getActiveTask();

}
