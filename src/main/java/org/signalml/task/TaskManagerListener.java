/* TaskManagerListener.java created 2007-10-06
 * 
 */

package org.signalml.task;

import java.util.EventListener;

/** TaskManagerListener
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TaskManagerListener extends EventListener {

	void taskAdded(TaskManagerEvent e);

	void taskRemoved(TaskManagerEvent e);
	
}
