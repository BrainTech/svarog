/* TaskManagerListener.java created 2007-10-06
 *
 */

package org.signalml.task;

import java.util.EventListener;

/**
 * A listener listening for {@link TaskManagerEvent TaskManagerEvents}
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TaskManagerListener extends EventListener {

	/**
	 * Invoked when new Task is being added
	 */
	void taskAdded(TaskManagerEvent e);

	/**
	 * Invoked when one of Tasks is being removed
	 */
	void taskRemoved(TaskManagerEvent e);

}
