/* TaskManagerEvent.java created 2007-10-06
 *
 */

package org.signalml.task;

import java.util.EventObject;

/** TaskManagerEvent
 * is used to notify that TaskManager has changed.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaskManagerEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private Task task;
	private int index;

        /**
         * Constructs new instance of TaskManagerEvent
         * @param source the object on which the Event initially occured
	 * @param task which was cause of the Event
	 * @param index index of the Event
	 */
	public TaskManagerEvent(Object source, Task task, int index) {
		super(source);
		this.task = task;
		this.index = index;
	}

        /**
         * Returns Task from this Event
         * @return Task from this Event
         */
	public Task getTask() {
		return task;
	}

        /**
         * Returns index of this Event
         * @return index of this Event
         */
	public int getIndex() {
		return index;
	}

}
