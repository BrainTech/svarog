/* TaskManagerEvent.java created 2007-10-06
 *
 */

package org.signalml.task;

import java.util.EventObject;

/** TaskManagerEvent
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaskManagerEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private Task task;
	private int index;

	public TaskManagerEvent(Object source, Task task, int index) {
		super(source);
		this.task = task;
		this.index = index;
	}

	public Task getTask() {
		return task;
	}

	public int getIndex() {
		return index;
	}

}
